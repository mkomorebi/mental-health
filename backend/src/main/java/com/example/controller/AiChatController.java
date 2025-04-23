package com.example.controller;

import com.example.common.Result;
import com.example.service.EmotionPromptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AI聊天控制器，处理与AI聊天相关的请求。
 */
@RestController
@RequestMapping("/business/chat")
public class AiChatController {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions"; // AI聊天API的URL
    private static final String API_KEY = "sk-8673586f1ee5487286126caca623eedc"; // API密钥
    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    private final RestTemplate restTemplate = new RestTemplate();
    
    @Autowired
    private EmotionPromptService emotionPromptService;

    /**
     * 处理AI聊天请求
     */
    @PostMapping("/aiChat")
    public Result aiChat(@RequestBody Map<String, Object> payload) throws JsonProcessingException {
        // 使用安全的类型转换
        @SuppressWarnings("unchecked")
        List<Map<String, String>> messages = payload.get("messages") instanceof List<?> 
            ? (List<Map<String, String>>) payload.get("messages") 
            : new ArrayList<>();
        
        // 获取语音情绪数据
        @SuppressWarnings("unchecked")
        Map<String, Object> voiceEmotion = payload.get("voiceEmotion") instanceof Map<?, ?> 
            ? (Map<String, Object>) payload.get("voiceEmotion") 
            : null;

        if (messages == null || messages.isEmpty()) {
            return Result.error("请选择一个对话");
        }
        
        // 添加详细日志，记录语音情绪数据
        if (voiceEmotion != null) {
            logger.info("收到语音情绪数据: 情绪={}, 置信度={}, 质量={}", 
                    voiceEmotion.get("emotion"), 
                    voiceEmotion.get("confidence"),
                    voiceEmotion.get("quality"));
        } else {
            logger.info("未收到语音情绪数据，将仅使用文本分析");
        }
        
        // 获取最后一条用户消息
        Map<String, String> lastUserMessage = null;
        for (int i = messages.size() - 1; i >= 0; i--) {
            if ("user".equals(messages.get(i).get("role"))) {
                lastUserMessage = messages.get(i);
                break;
            }
        }
        
        // 生成情绪感知提示词，传入语音情绪分析结果
        String emotionPrompt = emotionPromptService.generateEmotionPrompt(messages, voiceEmotion);
        logger.info("生成的情绪提示词长度: {} 字符", emotionPrompt != null ? emotionPrompt.length() : 0);
        logger.debug("生成的情绪提示词: {}", emotionPrompt);
        
        // 将情绪提示词添加到系统消息中
        if (emotionPrompt != null && !emotionPrompt.isEmpty() && lastUserMessage != null) {
            // 创建系统提示消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一位经验丰富、富有同理心的心理咨询师，名叫'心灵伙伴'。" +
                "请以自然、温暖的对话方式与用户交流，避免过于机械或刻板的回应。" +
                "使用适当的口语化表达，偶尔可以使用表情符号增加亲和力，但不要过度使用。" +
                "在回应中展现出真诚的关心，避免空洞的安慰，提供具体且个性化的建议。" +
                "当用户分享困难时，先表达理解和同理，再提供支持和建议。" +
                "记住，你的目标是让用户感到被倾听、被理解，并获得实质性的帮助。" + 
                emotionPrompt);
            
            // 将系统提示添加到消息列表的开头
            messages.add(0, systemMessage);
            
            logger.info("已添加情绪感知系统提示");
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7); // 控制生成文本的随机性

        // 创建HTTP头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.set("Content-Type", "application/json");

        // 创建HTTP实体
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);

            // 解析响应
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            // 获取 AI 回复内容
            String aiResponse = jsonNode.get("choices").get(0).get("message").get("content").asText();
            logger.info("AI响应: {}", aiResponse.substring(0, Math.min(100, aiResponse.length())) + "...");

            return Result.success(aiResponse);
        } catch (Exception e) {
            logger.error("AI服务请求失败", e);
            return Result.error("AI服务请求失败: " + e.getMessage());
        }
    }
}
