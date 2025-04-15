package com.example.audio;


import cn.xfyun.api.LfasrClient;
import cn.xfyun.config.LfasrTaskStatusEnum;
import cn.xfyun.model.response.lfasr.LfasrMessage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 讯飞开放平台语音转写客户端
 * 实现长语音文件的上传和转写功能
 * 
 * 主要功能：
 * 1. 创建语音转写客户端实例
 * 2. 上传音频文件获取任务ID
 * 3. 轮询查询转写进度
 * 4. 获取转写结果
 * 
 * 使用说明：
 * 1. APPID、SecretKey信息获取：https://console.xfyun.cn/services/lfasr
 * 2. 接口文档：https://www.xfyun.cn/doc/asr/ifasr_new/API.html
 * 
 * 注意事项：
 */
public class LfasrClientApp {
    private static final Logger logger = LoggerFactory.getLogger(LfasrClient.class);

    private static final String APP_ID = "13f26487";
    private static final String SECRET_KEY = "590a3b26743fed10603358e0b300d8f3";
    private static String AUDIO_FILE_PATH;

    static {
        try {
            AUDIO_FILE_PATH = LfasrClientApp.class.getResource("/").toURI().getPath() + "/audio/lfasr.wav";
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SignatureException, InterruptedException {
        invoke(AUDIO_FILE_PATH);
    }

    public static String invoke(String path) throws SignatureException, InterruptedException {
        // 1. 创建讯飞语音转写客户端实例，设置切片大小为102400字节
        LfasrClient lfasrClient = new LfasrClient.Builder(APP_ID, SECRET_KEY).slice(102400).build();

        // 2. 上传音频文件并获取任务ID
        LfasrMessage task = lfasrClient.upload(path);
        String taskId = task.getData();
        logger.info("转写任务 taskId：" + taskId);

        // 3. 轮询查询转写任务进度，直到任务完成（状态码为9）
        int status = 0;
        while (LfasrTaskStatusEnum.STATUS_9.getKey() != status) {
            LfasrMessage message = lfasrClient.getProgress(taskId);
            logger.info(message.toString());
            
            // 解析进度响应数据
            Gson gson = new Gson();
            Map<String, String> map = gson.fromJson(message.getData(), new TypeToken<Map<String, String>>() {
            }.getType());
            status = Integer.parseInt(map.get("status"));
            TimeUnit.SECONDS.sleep(2);  // 每2秒查询一次进度
        }
        
        // 4. 获取转写结果
        LfasrMessage result = lfasrClient.getResult(taskId);
        return result.getData();
    }
}
