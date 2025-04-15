package com.example.controller;

import com.example.common.Result;
import com.example.entity.EmotionResponse;
import com.example.service.EmotionApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.HashMap;
import java.util.Map;

/**
 * 情绪分析控制器，处理语音情绪分析请求
 */
@RestController
@RequestMapping("/business/emotion")
public class EmotionController {

    private static final Logger logger = LoggerFactory.getLogger(EmotionController.class);

    @Autowired
    private EmotionApiService emotionApiService;

    /**
     * 分析上传的音频文件，识别情绪
     * @param file 音频文件
     * @return 情绪分析结果
     */
    @PostMapping("/analyze")
    public Result analyzeEmotion(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("请求中没有音频文件或文件为空");
            return Result.error("请上传音频文件");
        }

        String originalFilename = file.getOriginalFilename();
        logger.info("收到情绪分析请求，文件名: {}, 大小: {} bytes, 内容类型: {}", 
                    originalFilename, file.getSize(), file.getContentType());

        try {
            // 检查文件格式
            String contentType = file.getContentType();
            if (contentType != null && !contentType.startsWith("audio/")) {
                logger.warn("上传的文件不是音频格式: {}", contentType);
                return Result.error("请上传音频格式的文件");
            }
            
            // 调用情绪API服务分析音频
            logger.info("开始调用情绪分析服务");
            EmotionResponse response = emotionApiService.predictEmotion(file).block();
            
            if (response == null) {
                logger.error("情绪分析服务未返回结果");
                return Result.error("情绪分析服务未返回结果");
            }

            // 验证返回的情绪是否是置信度最高的
            if (response.getProbabilities() != null && !response.getProbabilities().isEmpty()) {
                String highestEmotion = null;
                double highestProb = -1;
                
                for (Map.Entry<String, Double> entry : response.getProbabilities().entrySet()) {
                    if (entry.getValue() > highestProb) {
                        highestProb = entry.getValue();
                        highestEmotion = entry.getKey();
                    }
                }
                
                if (highestEmotion != null && !highestEmotion.equals(response.getEmotion())) {
                    logger.warn("API返回的情绪({})与概率最高的情绪({})不一致，已自动修正", 
                               response.getEmotion(), highestEmotion);
                    response.setEmotion(highestEmotion);
                }
            }

            logger.info("情绪分析完成: {}", response);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("emotion", response.getEmotion());
            result.put("probabilities", response.getProbabilities());
            result.put("text", response.getText());
            
            // 添加音频质量信息（如果有）
            if (response.getSnr() != null) {
                result.put("snr", response.getSnr());
            }
            if (response.getVolume() != null) {
                result.put("volume", response.getVolume());
            }
            if (response.getDuration() != null) {
                result.put("duration", response.getDuration());
            }

            return Result.success(result);
        } catch (Exception e) {
            logger.error("情绪分析失败", e);
            return Result.error("情绪分析失败: " + e.getMessage());
        }
    }
}