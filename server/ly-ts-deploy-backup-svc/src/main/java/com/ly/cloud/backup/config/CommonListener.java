package com.ly.cloud.backup.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Lazy
@Component
@Slf4j
public class CommonListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${fileClientPath}")
    private String fileClientPath;

    /**
     * 监听消费消息
     *
     * @param message
     */
    @RabbitListener(queues = "${queueName:queue_1}")
    public void consumeMessage(@Payload byte[] message) {
        try {
            String result = new String(message, "UTF-8");
            Map mapType = JSON.parseObject(result, Map.class);
            for (Object obj : mapType.keySet()) {
//                System.out.println("key为：" + obj + "值为：" + mapType.get(obj));
            }
            if (mapType.get("attach") != null) {
                write((String) mapType.get("attach"), (String) mapType.get("name"));
            }
        } catch (Exception e) {
            log.error("监听消费消息 发生异常： ", e);
        }
    }

    private void write(String attach, String name) throws IOException {
        File file = new File(fileClientPath, name);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] arr = decoder.decodeBuffer(attach);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file, false);
            os.write(arr, 0, arr.length);
            os.flush();
            os.close();
        } catch (IOException e1) {
            log.error(e1.getMessage(), e1);
        }
    }
}