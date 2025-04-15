package com.example.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件处理工具类，提供文件的读写、导出等功能。
 * 
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 私有构造函数，防止实例化
public class FileUtils extends FileUtil {

    /**
     * 将MultipartFile转换为File
     *
     * @param tempDir 临时目录
     * @param multiFile 上传的文件
     * @return 转换后的File对象
     */
    public static File transferMultipartFileToFile(String tempDir, MultipartFile multiFile) {
        String path = tempDir + multiFile.getOriginalFilename(); // 获取文件路径
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile(); // 创建新文件
            }
            FileCopyUtils.copy(multiFile.getBytes(), file); // 复制文件内容
        } catch (Exception e) {
            throw new RuntimeException(e); // 抛出运行时异常
        }
        return file; // 返回File对象
    }

    /**
     * 导出文件
     *
     * @param response HttpServletResponse对象
     * @param fileName 文件名
     * @param filePath 文件路径
     * @throws IOException IO异常
     */
    public static void exportFile(HttpServletResponse response, String fileName, String filePath) throws IOException {
        setAttachmentResponseHeader(response, fileName); // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8"); // 设置内容类型
        BufferedInputStream inputStream = FileUtils.getInputStream(filePath); // 获取输入流
        IoUtil.copy(inputStream, response.getOutputStream(), inputStream.available()); // 复制文件内容到响应输出流
        response.setContentLength(inputStream.available()); // 设置内容长度
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName); // 编码文件名

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
            .append(percentEncodedFileName)
            .append(";")
            .append("filename*=")
            .append("utf-8''")
            .append(percentEncodedFileName);

        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename"); // 设置跨域响应头
        response.setHeader("Content-disposition", contentDispositionValue.toString()); // 设置内容处置头
        response.setHeader("download-filename", percentEncodedFileName); // 设置下载文件名
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString()); // 编码字符串
        return encode.replaceAll("\\+", "%20"); // 替换加号为空格
    }

    /**
     * 将内容写入报告文件
     *
     * @param content 内容
     * @param filePath 文件路径
     */
    public static void writeContent2Report(String content, String filePath) {
        BufferedWriter out = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // 创建父目录
                file.createNewFile(); // 创建新文件
            }
            out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath, true), "UTF-8")); // 设置编码格式
            out.write(content + "\r\n"); // 写入内容
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常信息
        } finally {
            try {
                if (out != null) {
                    out.close(); // 关闭流
                }
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
        }
    }
}
