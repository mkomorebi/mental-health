package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 情绪提示词服务，用于生成基于用户情绪的提示词
 */
@Service
public class EmotionPromptService {
    private static final Logger logger = LoggerFactory.getLogger(EmotionPromptService.class);
    
    // 存储用户历史情绪记录
    private final Map<String, List<EmotionRecord>> userEmotionHistory = new HashMap<>();
    
    // 负面情绪列表
    private static final Set<String> NEGATIVE_EMOTIONS = new HashSet<>(
            Arrays.asList("愤怒", "悲伤", "焦虑", "恐惧", "抑郁", "沮丧", "压力", "厌恶")
    );
    
    // 高风险情绪列表
    private static final Set<String> HIGH_RISK_EMOTIONS = new HashSet<>(
            Arrays.asList("抑郁", "恐惧", "绝望")
    );

    // 权重配置
    private static final double DEFAULT_VOICE_WEIGHT = 0.4;
    private static final double DEFAULT_TEXT_WEIGHT = 0.6;
    private static final double LOW_QUALITY_VOICE_WEIGHT = 0.2;
    
    // 置信度阈值
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.8;
    private static final double MEDIUM_CONFIDENCE_THRESHOLD = 0.5;

    /**
     * 生成情绪感知提示词
     * @param messages 消息历史
     * @param voiceEmotion 语音情绪分析结果（可选）
     * @return 情绪提示词
     */
    public String generateEmotionPrompt(List<Map<String, String>> messages, Map<String, Object> voiceEmotion) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }

        // 提取用户ID（如果有的话）
        String userId = extractUserId(messages);
        
        // 分析文本情绪
        EmotionResult textEmotionResult = analyzeTextEmotion(messages);
        String textEmotion = textEmotionResult.emotion;
        double textConfidence = textEmotionResult.confidence;
        
        logger.info("文本情绪分析结果: {}, 置信度: {}", textEmotion, textConfidence);
        
        // 分析语音情绪（如果有）
        String voiceEmotionType = null;
        double voiceConfidence = 0.0;
        boolean isLowQualityAudio = false;
        
        if (voiceEmotion != null) {
            logger.info("收到语音情绪数据: {}", voiceEmotion);
            voiceEmotionType = (String) voiceEmotion.get("emotion");
            String confidenceStr = (String) voiceEmotion.get("confidence");
            
            // 检查是否是低质量音频
            if (voiceEmotion.containsKey("quality")) {
                String quality = (String) voiceEmotion.get("quality");
                isLowQualityAudio = "low".equalsIgnoreCase(quality);
            }
            
            if (confidenceStr != null) {
                logger.info("解析语音情绪置信度字符串: {}", confidenceStr);
                if (confidenceStr.endsWith("%")) {
                    try {
                        voiceConfidence = Double.parseDouble(confidenceStr.substring(0, confidenceStr.length() - 1)) / 100.0;
                        logger.info("解析后的置信度值: {}", voiceConfidence);
                    } catch (NumberFormatException e) {
                        logger.warn("无法解析语音情绪置信度: {}", confidenceStr, e);
                    }
                } else {
                    logger.warn("语音情绪置信度格式不正确，应以%结尾: {}", confidenceStr);
                }
            }
            
            logger.info("语音情绪分析结果: {}, 置信度: {}, 低质量音频: {}", 
                    voiceEmotionType, voiceConfidence, isLowQualityAudio);
        }
        
        // 确定最终情绪和置信度
        EmotionDecision decision = makeEmotionDecision(
                textEmotion, textConfidence, 
                voiceEmotionType, voiceConfidence, 
                isLowQualityAudio);
        
        String finalEmotion = decision.emotion;
        double finalConfidence = decision.confidence;
        String decisionSource = decision.source;
        
        logger.info("最终情绪决策: {}, 置信度: {}, 来源: {}", 
                finalEmotion, finalConfidence, decisionSource);
        
        // 检查情绪矛盾
        boolean emotionsConflict = false;
        if (voiceEmotionType != null && !voiceEmotionType.equals(textEmotion)) {
            emotionsConflict = true;
            logger.info("检测到情绪矛盾: 语音情绪={}, 文本情绪={}", voiceEmotionType, textEmotion);
        }
        
        // 更新用户情绪历史
        updateEmotionHistory(userId, finalEmotion, finalConfidence);
        
        // 检查连续负面情绪
        boolean hasConsecutiveNegativeEmotions = checkConsecutiveNegativeEmotions(userId);
        
        // 构建情绪提示词
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("我检测到用户当前的情绪可能是: ").append(finalEmotion)
                    .append("（基于").append(decisionSource).append("分析，置信度: ")
                    .append(String.format("%.2f", finalConfidence)).append("）。\n\n");

        // 添加人性化回复指导
        promptBuilder.append("在回复时，请注意以下几点：\n");
        promptBuilder.append("1. 使用自然、流畅的对话风格，就像一位真实的心理咨询师\n");
        promptBuilder.append("2. 避免过于刻板或公式化的回应\n");
        promptBuilder.append("3. 适当使用一些口语化表达，如'嗯'、'我理解'、'确实如此'\n");
        promptBuilder.append("4. 可以偶尔使用表情符号增加亲和力，但不要过度\n");
        promptBuilder.append("5. 展现出真诚的关心，而不是机械的同情\n\n");

        // 根据置信度添加不同级别的响应策略
        if (finalConfidence > HIGH_CONFIDENCE_THRESHOLD) {
            // 高置信度响应
            promptBuilder.append("【高置信度响应】用户情绪信号非常明确");
            if (HIGH_RISK_EMOTIONS.contains(finalEmotion) || NEGATIVE_EMOTIONS.contains(finalEmotion)) {
                promptBuilder.append("，且情绪状态需要特别关注。请触发高风险关怀流程：\n");
                promptBuilder.append("- 表达深切的关心和理解\n");
                promptBuilder.append("- 提供即时的情绪支持\n");
                promptBuilder.append("- 询问是否有自伤或危险想法\n");
                promptBuilder.append("- 建议专业心理健康资源\n");
                promptBuilder.append("- 提供危机干预热线信息\n\n");
            } else {
                promptBuilder.append("。请提供针对性的回应，充分肯定用户的情绪体验。\n\n");
            }
        } else if (finalConfidence > MEDIUM_CONFIDENCE_THRESHOLD) {
            // 中等置信度响应
            promptBuilder.append("【中等置信度响应】用户情绪信号较为明确，但需要温和确认：\n");
            promptBuilder.append("- 可以使用'我感觉你似乎有些").append(finalEmotion).append("，是这样吗？'\n");
            promptBuilder.append("- 或'听起来这件事让你感到").append(finalEmotion).append("，我理解得对吗？'\n");
            promptBuilder.append("- 给用户空间澄清或表达更多情绪细节\n\n");
        } else {
            // 低置信度响应
            promptBuilder.append("【低置信度响应】用户情绪信号不够明确，请：\n");
            promptBuilder.append("- 避免直接假设用户情绪状态\n");
            promptBuilder.append("- 使用开放性问题了解用户感受\n");
            promptBuilder.append("- 保持中性、支持性的回应\n");
            promptBuilder.append("- 关注用户表达的具体内容而非情绪标签\n\n");
        }

        // 添加情绪矛盾提示
        if (emotionsConflict) {
            promptBuilder.append("【情绪矛盾提示】用户的语音情绪(").append(voiceEmotionType)
                        .append(")与文本表达的情绪(").append(textEmotion)
                        .append(")存在差异。可以温和地探询，如'我感觉你的语气听起来有些")
                        .append(voiceEmotionType).append("，但你提到的内容似乎更偏向")
                        .append(textEmotion).append("，能和我分享一下你现在的真实感受吗？'\n\n");
        }
        
        // 添加连续负面情绪提示
        if (hasConsecutiveNegativeEmotions) {
            promptBuilder.append("【连续负面情绪警示】用户已连续多次表现出负面情绪。请特别关注其心理状态，提供更深入的支持。可以温和地询问'最近发生了什么让你持续感到")
                        .append(finalEmotion).append("？'或'有什么我能帮到你的吗？'。如果情况严重，可以建议'有时候，与专业心理咨询师交流可能会有所帮助。'\n\n");
        }
        
        // 根据情绪类型添加具体建议
        promptBuilder.append(getEmotionSpecificGuidance(finalEmotion));
        
        return promptBuilder.toString();
    }
    
    /**
     * 情绪决策类
     */
    private static class EmotionDecision {
        String emotion;
        double confidence;
        String source;
        
        EmotionDecision(String emotion, double confidence, String source) {
            this.emotion = emotion;
            this.confidence = confidence;
            this.source = source;
        }
    }
    
    /**
     * 情绪结果类
     */
    private static class EmotionResult {
        String emotion;
        double confidence;
        
        EmotionResult(String emotion, double confidence) {
            this.emotion = emotion;
            this.confidence = confidence;
        }
    }
    
    /**
     * 基于权重和置信度做出情绪决策
     */
    private EmotionDecision makeEmotionDecision(
            String textEmotion, double textConfidence,
            String voiceEmotion, double voiceConfidence,
            boolean isLowQualityAudio) {
        
        // 如果没有语音情绪，100%依赖文本分析
        if (voiceEmotion == null) {
            return new EmotionDecision(textEmotion, textConfidence, "文本");
        }
        
        // 确定语音权重 - 低质量音频降低权重
        double voiceWeight = isLowQualityAudio ? LOW_QUALITY_VOICE_WEIGHT : DEFAULT_VOICE_WEIGHT;
        double textWeight = 1.0 - voiceWeight;
        
        // 如果情绪相同，合并置信度
        if (voiceEmotion.equals(textEmotion)) {
            double combinedConfidence = (voiceConfidence * voiceWeight) + (textConfidence * textWeight);
            return new EmotionDecision(textEmotion, combinedConfidence, "综合");
        }
        
        // 情绪不同，计算加权得分
        double voiceScore = voiceConfidence * voiceWeight;
        double textScore = textConfidence * textWeight;
        
        if (voiceScore > textScore) {
            return new EmotionDecision(voiceEmotion, voiceConfidence, "语音(权重决策)");
        } else {
            return new EmotionDecision(textEmotion, textConfidence, "文本(权重决策)");
        }
    }
    
    /**
     * 提取用户ID
     */
    private String extractUserId(List<Map<String, String>> messages) {
        // 实际应用中，这里应该从消息或会话中提取用户ID
        // 简化实现，使用第一条用户消息的哈希值作为ID
        for (Map<String, String> message : messages) {
            if ("user".equals(message.get("role"))) {
                return String.valueOf(message.get("content").hashCode());
            }
        }
        return "unknown-user";
    }
    
    /**
     * 分析文本情绪
     */
    private EmotionResult analyzeTextEmotion(List<Map<String, String>> messages) {
        // 获取最近的用户消息
        List<String> userMessages = new ArrayList<>();
        for (int i = messages.size() - 1; i >= 0 && userMessages.size() < 3; i--) {
            Map<String, String> message = messages.get(i);
            if ("user".equals(message.get("role"))) {
                userMessages.add(message.get("content"));
            }
        }
        
        if (userMessages.isEmpty()) {
            return new EmotionResult("平静", 0.5);
        }
        
        // 简单的关键词匹配情绪分析
        String combinedText = String.join(" ", userMessages);
        
        Map<String, Integer> emotionCounts = new HashMap<>();
        emotionCounts.put("开心", countKeywords(combinedText, "开心", "高兴", "快乐", "兴奋", "满意", "喜欢", "爱", "感谢", "谢谢", "棒", "好"));
        emotionCounts.put("平静", countKeywords(combinedText, "平静", "放松", "安心", "舒适", "平和", "安宁"));
        emotionCounts.put("焦虑", countKeywords(combinedText, "焦虑", "担心", "紧张", "不安", "害怕", "恐惧", "怕", "忧虑"));
        emotionCounts.put("愤怒", countKeywords(combinedText, "愤怒", "生气", "恼火", "烦躁", "讨厌", "恨", "烦", "气", "不爽"));
        emotionCounts.put("悲伤", countKeywords(combinedText, "悲伤", "难过", "伤心", "痛苦", "失望", "沮丧", "哭", "泪", "遗憾"));
        emotionCounts.put("困惑", countKeywords(combinedText, "困惑", "疑惑", "不解", "迷茫", "不确定", "不明白", "不懂", "为什么"));
        emotionCounts.put("压力", countKeywords(combinedText, "压力", "累", "疲惫", "劳累", "负担", "重担", "不堪重负", "压抑"));
        emotionCounts.put("抑郁", countKeywords(combinedText, "抑郁", "绝望", "无助", "无望", "活不下去", "想死", "自杀", "结束生命"));
        
        // 找出最高频的情绪
        String dominantEmotion = "平静"; // 默认情绪
        int maxCount = 0;
        int totalKeywords = 0;
        
        for (Map.Entry<String, Integer> entry : emotionCounts.entrySet()) {
            totalKeywords += entry.getValue();
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantEmotion = entry.getKey();
            }
        }
        
        // 计算置信度 - 基于关键词匹配的比例和数量
        double confidence = 0.5; // 默认中等置信度
        
        if (totalKeywords > 0) {
            // 关键词匹配比例
            double ratio = (double) maxCount / totalKeywords;
            
            // 关键词数量因子 (最多5个关键词达到满分)
            double countFactor = Math.min(1.0, maxCount / 5.0);
            
            // 综合计算置信度
            confidence = 0.3 + (ratio * 0.4) + (countFactor * 0.3);
            
            // 确保在0.3-0.9范围内
            confidence = Math.max(0.3, Math.min(0.9, confidence));
        }
        
        return new EmotionResult(dominantEmotion, confidence);
    }
    
    /**
     * 计算文本中关键词出现的次数
     */
    private int countKeywords(String text, String... keywords) {
        int count = 0;
        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile(keyword);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 更新用户情绪历史
     */
    private void updateEmotionHistory(String userId, String emotion, double confidence) {
        if (!userEmotionHistory.containsKey(userId)) {
            userEmotionHistory.put(userId, new ArrayList<>());
        }
        
        List<EmotionRecord> history = userEmotionHistory.get(userId);
        history.add(new EmotionRecord(emotion, confidence, System.currentTimeMillis()));
        
        // 只保留最近10条记录
        if (history.size() > 10) {
            history.remove(0);
        }
    }
    
    /**
     * 检查是否有连续3次或以上的负面情绪
     */
    private boolean checkConsecutiveNegativeEmotions(String userId) {
        if (!userEmotionHistory.containsKey(userId)) {
            return false;
        }
        
        List<EmotionRecord> history = userEmotionHistory.get(userId);
        if (history.size() < 3) {
            return false;
        }
        
        // 检查最近3条记录
        int negativeCount = 0;
        for (int i = history.size() - 1; i >= Math.max(0, history.size() - 3); i--) {
            EmotionRecord record = history.get(i);
            // 只考虑置信度>0.5的情绪记录
            if (record.confidence > MEDIUM_CONFIDENCE_THRESHOLD && NEGATIVE_EMOTIONS.contains(record.emotion)) {
                negativeCount++;
            }
        }
        
        return negativeCount >= 3;
    }
    
    /**
     * 根据情绪类型提供具体指导
     */
    private String getEmotionSpecificGuidance(String emotion) {
        switch (emotion) {
            case "开心":
                return "【情绪指导：开心】用户当前情绪积极愉悦。你可以：\n" +
                       "- 真诚地分享用户的喜悦，使用温暖的语气\n" +
                       "- 鼓励用户分享更多让他们开心的事情\n" +
                       "- 适当使用轻松活泼的语言，可以加入一两个笑脸表情\n" +
                       "- 帮助用户思考如何保持这种积极情绪";
                       
            case "平静":
                return "【情绪指导：平静】用户当前情绪平稳。你可以：\n" +
                       "- 使用平和、从容的语气交流\n" +
                       "- 提供有深度的思考和建议\n" +
                       "- 可以探讨一些需要思考的话题\n" +
                       "- 保持对话的流畅性和连贯性";
                       
            case "焦虑":
                return "【情绪指导：焦虑】用户可能正在经历焦虑。请注意：\n" +
                       "- 使用平静、稳定且有节奏的语言\n" +
                       "- 首先确认和理解他们的焦虑感受\n" +
                       "- 避免使用'不要担心'这类轻描淡写的安慰\n" +
                       "- 可以温和地引导用户进行简单的呼吸练习\n" +
                       "- 帮助用户将大问题分解成小步骤\n" +
                       "- 提供具体的、可行的建议，而不是抽象的指导";
                       
            case "愤怒":
                return "【情绪指导：愤怒】用户可能正在感到愤怒或烦躁。请注意：\n" +
                       "- 保持冷静、尊重的态度，不要显得防御性\n" +
                       "- 首先完全接纳用户的情绪，表达'我理解你感到生气是有原因的'\n" +
                       "- 给予用户充分表达的空间\n" +
                       "- 避免争辩或试图立即'解决'问题\n" +
                       "- 当用户情绪稍缓和时，可以温和地引导他们思考愤怒背后的真正原因\n" +
                       "- 使用'我能理解这种情况会让人感到沮丧'这类表达，而不是'你不应该这么生气'";
                       
            case "悲伤":
                return "【情绪指导：悲伤】用户可能正在经历悲伤或失落。请注意：\n" +
                       "- 使用温柔、关怀的语气\n" +
                       "- 表达真诚的同理心，如'我能感受到这对你来说很难过'\n" +
                       "- 给予情感上的支持和陪伴，不要急于提供解决方案\n" +
                       "- 避免过度乐观或轻描淡写用户的感受\n" +
                       "- 可以分享一些应对悲伤的健康方式\n" +
                       "- 肯定用户表达情感的勇气";
                       
            case "困惑":
                return "【情绪指导：困惑】用户可能感到困惑或不确定。请注意：\n" +
                       "- 使用清晰、简洁的语言\n" +
                       "- 耐心解释，避免使用专业术语\n" +
                       "- 可以使用比喻或例子来帮助理解\n" +
                       "- 分步骤引导思考\n" +
                       "- 确认用户是否理解你的回应\n" +
                       "- 鼓励用户提出更具体的问题";
                       
            case "压力":
                return "【情绪指导：压力】用户可能正在承受压力。请注意：\n" +
                       "- 首先承认压力感受的合理性\n" +
                       "- 使用支持性的语言，如'面对这么多任务，感到压力是很自然的'\n" +
                       "- 鼓励短暂休息和自我关爱\n" +
                       "- 提供实用的压力管理技巧，如优先级排序、深呼吸或正念练习\n" +
                       "- 引导用户思考哪些因素在他们的控制范围内\n" +
                       "- 建议适当寻求社交支持";
                       
            case "抑郁":
                return "【情绪指导：抑郁-高风险】用户可能正在经历抑郁情绪。请特别注意：\n" +
                       "- 使用温和、支持性的语言，避免过于轻松或强制性的语气\n" +
                       "- 表达真诚的关心，如'听到你这样说，我很关心你'\n" +
                       "- 肯定用户寻求交流的勇气\n" +
                       "- 温和地询问是否有自伤想法，如'你是否有过伤害自己的想法？'\n" +
                       "- 提供危机干预资源，如心理健康热线\n" +
                       "- 鼓励寻求专业帮助\n" +
                       "- 强调抑郁是可以治疗的，并分享一些小的自我照顾建议";
                       
            default:
                return "【情绪指导：通用】请以自然、温暖的方式回应用户，展现真诚的关心和理解。避免过于正式或机械的语言，使用适当的口语化表达增加亲和力。在提供建议时，确保它们是具体、实用且个性化的。";
        }
    }
    
    /**
     * 情绪记录内部类
     */
    private static class EmotionRecord {
        String emotion;
        double confidence;
        long timestamp;
        
        EmotionRecord(String emotion, double confidence, long timestamp) {
            this.emotion = emotion;
            this.confidence = confidence;
            this.timestamp = timestamp;
        }
    }
    
    /**
     * 重载方法，兼容旧版本调用
     */
    public String generateEmotionPrompt(List<Map<String, String>> messages) {
        return generateEmotionPrompt(messages, null);
    }
} 