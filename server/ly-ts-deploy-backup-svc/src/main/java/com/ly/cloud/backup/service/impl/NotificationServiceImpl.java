package com.ly.cloud.backup.service.impl;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    // 从 application.yml 中获取发件人邮箱地址
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 发送备份失败的通知邮件
     * @param recipientEmail 收件人邮箱
     * @param strategyTitle  失败的策略标题
     * @param failureReason  失败原因
     */
//    @Async // 使用异步执行，避免阻塞主线程
    public void sendBackupFailureNotification(String recipientEmail, String strategyTitle, String failureReason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("【备份系统预警】备份任务失败通知");

            String content = String.format(
                    "您好，\n\n" +
                            "服务器备份系统检测到以下备份任务执行失败：\n\n" +
                            "  - 备份策略名称: %s\n" +
                            "  - 失败原因: %s\n\n" +
                            "请及时登录系统检查并处理。\n\n" +
                            "此邮件为系统自动发送，请勿回复。",
                    strategyTitle,
                    failureReason
            );
            message.setText(content);

            mailSender.send(message);
            logger.info("成功发送备份失败预警邮件至: {}", recipientEmail);
        } catch (Exception e) {
            logger.error("发送备份失败预警邮件时发生错误", e);
        }
    }
}
