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

@Service
public class EmotionApiService {

    private final WebClient webClient;
    
    @Value("${emotion.api.url:http://localhost:5000/predict}")
    private String emotionApiUrl;
    
    @Value("${file.upload.dir:${user.dir}/files/temp}")
    private String fileUploadDir;

    private static final Logger logger = LoggerFactory.getLogger(EmotionApiService.class);

    public EmotionApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<EmotionResponse> predictEmotion(MultipartFile file) {
        logger.info("开始情绪分析，文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        return processWithTempFile(file)
                .doOnSuccess(response -> {
                    logger.info("情绪分析成功: 检测到情绪 '{}', 状态: {}", 
                            response.getEmotion(), response.getStatus());
                })
                .doOnError(e -> {
                    logger.error("情绪分析API调用失败: {}", e.getMessage(), e);
                });
    }
    
    private Mono<EmotionResponse> processWithTempFile(MultipartFile file) {
        File tempFile = null;
        try {
            // 确保目录存在
            File uploadDir = new File(fileUploadDir);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    logger.error("无法创建上传目录: {}", fileUploadDir);
                    return Mono.error(new RuntimeException("无法创建上传目录"));
                }
            }
            
            // 在我们自己的目录中创建临时文件
            String originalFilename = file.getOriginalFilename();
            String filename = originalFilename != null ? originalFilename : "audio.wav";
            String timestamp = String.valueOf(System.currentTimeMillis());
            tempFile = new File(uploadDir, timestamp + "_" + filename);
            
            // 立即将文件内容写入我们的临时文件
            file.transferTo(tempFile);
            
            logger.info("已将文件保存到临时位置: {}", tempFile.getAbsolutePath());
            
            final File finalTempFile = tempFile;
            
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(tempFile))
                   .filename(filename);
            
            logger.debug("发送请求到情绪API: {}", emotionApiUrl);
            
            return webClient.post()
                    .uri(emotionApiUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .flatMap(error -> {
                                        logger.error("API返回错误: {} - {}", response.statusCode(), error);
                                        return Mono.error(new RuntimeException("API调用失败: " + error));
                                    }))
                    .bodyToMono(EmotionResponse.class)
                    .doOnNext(response -> {
                        logger.debug("API响应: emotion={}, status={}", 
                                response.getEmotion(), response.getStatus());
                    })
                    .doFinally(signal -> {
                        // 无论成功还是失败，都删除临时文件
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
            // 如果创建临时文件失败，也要尝试清理
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            logger.error("处理文件时出错: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("处理文件时出错: " + e.getMessage()));
        }
    }
}