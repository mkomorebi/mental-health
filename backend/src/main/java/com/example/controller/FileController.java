package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.audio.LfasrClientApp;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件控制器，处理文件上传和下载相关的请求。
 */
@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private static final String filePath = System.getProperty("user.dir") + File.separator + "files" + File.separator;

    @Value("${fileBaseUrl:}")
    private String fileBaseUrl; // 文件基础URL

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }
        
        try {
            // 确保目录存在
            File dir = new File(filePath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("无法创建目录: " + filePath);
                    return Result.error("文件上传失败：无法创建目录");
                }
            }
            
            // 获取文件类型
            String contentType = file.getContentType();
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 根据文件类型确定正确的MIME类型
            String mimeType = contentType;
            if (contentType.equals("image/jpg")) {
                mimeType = "image/jpeg"; // 修正常见错误
            }
            
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            String newFileName = uuid + suffix;
            
            // 读取文件内容
            byte[] fileBytes = file.getBytes();
            log.info("文件大小: {} 字节", fileBytes.length);
            
            // 保存文件
            File destFile = new File(dir, newFileName);
            log.info("尝试保存文件到: " + destFile.getAbsolutePath());
            FileUtil.writeBytes(fileBytes, destFile);
            
            // 返回可访问的URL
            String url = fileBaseUrl + "/files/download/" + newFileName;
            
            log.info("文件上传成功，URL: " + url);
            
            // 返回结果 - 确保返回格式一致
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        OutputStream os = null;
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                // 处理特殊前缀，用于区分不同类型的文件
                String actualFileName = fileName;
                String fileDirPath = filePath;
                
                // 如果是反馈图片
                if (fileName.startsWith("Feedbackimgs_")) {
                    actualFileName = fileName.substring("Feedbackimgs_".length());
                    fileDirPath = filePath + "Feedbackimgs" + File.separator;
                }
                
                // 根据文件扩展名设置内容类型
                String contentType = "application/octet-stream";
                if (actualFileName.toLowerCase().endsWith(".jpg") || actualFileName.toLowerCase().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (actualFileName.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (actualFileName.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (actualFileName.toLowerCase().endsWith(".pdf")) {
                    contentType = "application/pdf";
                }
                response.setContentType(contentType);
                
                // 对于PDF和图片，允许跨域访问
                if (contentType.startsWith("image/") || contentType.equals("application/pdf")) {
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "*");
                }
                
                // 对于图片和PDF，不设置Content-Disposition头，这样浏览器会直接显示而不是下载
                if (contentType.startsWith("application/") && !contentType.equals("application/pdf")) {
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(actualFileName, StandardCharsets.UTF_8));
                }
                
                File file = new File(fileDirPath + actualFileName);
                if (!file.exists()) {
                    log.error("文件不存在: " + fileDirPath + actualFileName);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                
                // 读取并输出文件
                byte[] bytes = FileUtil.readBytes(file);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                
                log.info("文件下载成功: " + fileDirPath + actualFileName);
            }
        } catch (Exception e) {
            log.error("文件下载失败: " + fileName, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception ex) {
                log.error("设置响应状态失败", ex);
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭输出流失败", e);
                }
            }
        }
    }

    /**
     * 翻译音频
     */
    @PostMapping("/transcription")
    public Result transcription(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            log.error("文件名为空");
            return Result.error("文件名不能为空");
        }
        
        log.info("收到音频转写请求，文件名: {}, 大小: {} bytes, 内容类型: {}", 
                 originalFilename, file.getSize(), file.getContentType());
        
        try {
            // 确保目录存在
            File dir = new File(filePath);
            if (!dir.exists()) {
                log.info("创建文件目录: {}", filePath);
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("无法创建目录: {}", filePath);
                    return Result.error("文件上传失败：无法创建目录");
                }
            }
            
            // 生成唯一文件名
            String timestamp = System.currentTimeMillis() + "";
            String fileExtension = originalFilename.contains(".") ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".wav";
            String fileName = timestamp + "-" + originalFilename;
            String realFilePath = filePath + fileName;
            
            log.info("保存音频文件到: {}", realFilePath);
            
            // 保存文件 - 使用transferTo而不是读取字节
            File destFile = new File(realFilePath);
            file.transferTo(destFile);
            
            // 检查文件是否成功保存
            if (!destFile.exists() || destFile.length() == 0) {
                log.error("文件保存失败或文件大小为0: {}", realFilePath);
                return Result.error("文件保存失败");
            }
            
            log.info("开始音频转写处理");
            
            // 调用语音转译服务
            String res = LfasrClientApp.invoke(realFilePath);
            
            // 检查转写结果
            if (res == null || res.isEmpty()) {
                log.error("转写结果为空");
                return Result.error("转写失败：未获取到结果");
            }
            
            log.info("转写原始结果: {}", res);
            
            // 解析转写结果
            List<JSONObject> objs;
            try {
                objs = JSONArray.parseArray(res, JSONObject.class);
            } catch (Exception e) {
                log.error("转写结果解析失败: {}", e.getMessage());
                return Result.error("转写结果解析失败");
            }

            // 提取文本内容
            List<String> contents = objs.stream()
                    .filter(x -> Strings.isNotBlank(x.getString("onebest")))
                    .map(x -> x.getString("onebest"))
                    .collect(Collectors.toList());
            
            String result = CollUtil.join(contents, "，");
            log.info("转写完成，结果: {}", result);
            
            // 清理临时文件
            try {
                FileUtil.del(realFilePath);
                log.info("临时文件已删除: {}", realFilePath);
            } catch (Exception e) {
                log.warn("临时文件删除失败: {}", e.getMessage());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("音频转写处理失败", e);
            return Result.error("翻译失败: " + e.getMessage());
        }
    }

    /**
     * wang-editor编辑器文件上传接口
     */
    @PostMapping("/wang/upload")
    public Map<String, Object> wangEditorUpload(MultipartFile file) {
        String flag = System.currentTimeMillis() + "";
        String fileName = file.getOriginalFilename();
        try {
            // 文件存储形式：时间戳-文件名
            FileUtil.writeBytes(file.getBytes(), filePath + flag + "-" + fileName);
            System.out.println(fileName + "--上传成功");
            Thread.sleep(1L);
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        String http = fileBaseUrl + "/files/download/";
        Map<String, Object> resMap = new HashMap<>();
        // wangEditor上传图片成功后， 需要返回的参数
        resMap.put("errno", 0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url", http + flag + "-" + fileName)));
        return resMap;
    }
}
