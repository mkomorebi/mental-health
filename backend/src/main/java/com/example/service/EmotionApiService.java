package com.example.service;

import com.example.entity.EmotionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;

/**
 * 情绪分析服务
 * 负责与情绪分析API交互，发送音频文件并获取情绪分析结果
 */
@Service
public class EmotionApiService {

    // WebClient实例用于发送HTTP请求
    private final WebClient webClient;
    
    // 从配置文件中注入情绪分析API的URL，默认为本地5000端口
    @Value("${emotion.api.url:http://localhost:5000/predict}")
    private String emotionApiUrl;
    
    // 从配置文件中注入临时文件上传目录，默认为应用运行目录下的files/temp
    @Value("${file.upload.dir:${user.dir}/files/temp}")
    private String fileUploadDir;

    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(EmotionApiService.class);

    /**
     * 构造函数，初始化WebClient
     * @param webClientBuilder WebClient构建器，由Spring自动注入
     */
    public EmotionApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 情绪预测的主方法，接收音频文件并返回情绪分析结果
     * @param file 上传的音频文件
     * @return 包含情绪分析结果的Mono对象
     */
    public Mono<EmotionResponse> predictEmotion(MultipartFile file) {
        logger.info("开始情绪分析，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        return processWithTempFile(file)
                .doOnSuccess(response -> {
                    // 成功时记录情绪分析结果
                    logger.info("情绪分析成功: 检测到情绪 '{}', 状态: {}", 
                            response.getEmotion(), response.getStatus());
                })
                .doOnError(e -> {
                    // 失败时记录错误信息
                    logger.error("情绪分析API调用失败: {}", e.getMessage(), e);
                });
    }
    
    /**
     * 处理上传的文件并调用情绪分析API
     * 将MultipartFile保存为临时文件，然后发送到API，最后清理临时文件
     * @param file 上传的音频文件
     * @return 包含情绪分析结果的Mono对象
     */
    private Mono<EmotionResponse> processWithTempFile(MultipartFile file) {
        File tempFile = null;
        try {
            // 确保临时文件目录存在
            File uploadDir = new File(fileUploadDir);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    logger.error("无法创建上传目录: {}", fileUploadDir);
                    return Mono.error(new RuntimeException("无法创建上传目录"));
                }
            }
            
            // 创建带时间戳的临时文件，确保文件名唯一
            String originalFilename = file.getOriginalFilename();
            String filename = originalFilename != null ? originalFilename : "audio.wav";
            String timestamp = String.valueOf(System.currentTimeMillis());
            tempFile = new File(uploadDir, timestamp + "_" + filename);
            
            // 将上传的文件内容写入临时文件
            file.transferTo(tempFile);
            
            logger.info("已将文件保存到临时位置: {}", tempFile.getAbsolutePath());
            
            final File finalTempFile = tempFile;
            
            // 构建多部分表单数据，包含文件
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(tempFile))
                   .filename(filename);
            
            logger.debug("发送请求到情绪API: {}", emotionApiUrl);
            
            // 使用WebClient发送POST请求到情绪分析API
            return webClient.post()
                    .uri(emotionApiUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    // 处理API返回的错误状态
                    .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.error("API返回错误: {} - {}", response.statusCode(), error);
                                        return Mono.error(new RuntimeException("API调用失败: " + error));
                                    }))
                    // 将API响应转换为EmotionResponse对象
                    .bodyToMono(EmotionResponse.class)
                    .doOnNext(response -> {
                        // 记录API响应结果
                        logger.debug("API响应: emotion={}, status={}", 
                                response.getEmotion(), response.getStatus());
                    })
                    .doFinally(signal -> {
                        // 无论请求成功还是失败，都删除临时文件
                        if (finalTempFile != null && finalTempFile.exists()) {
                            boolean deleted = finalTempFile.delete();
                            if (deleted) {
                                logger.debug("临时文件已删除: {}", finalTempFile.getAbsolutePath());
                            } else {
                                logger.warn("无法删除临时文件: {}", finalTempFile.getAbsolutePath());
                            }
                        }
                    });
        } catch (IOException e) {
            // 处理文件IO异常
            // 如果创建临时文件失败，也要尝试清理
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            logger.error("处理文件时出错: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("处理文件时出错: " + e.getMessage()));
        }
    }
}