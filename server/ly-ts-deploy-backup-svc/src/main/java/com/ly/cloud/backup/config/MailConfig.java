package com.ly.cloud.backup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // 从 application.properties/yml 中读取配置
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password); // 注意：这里应该是你的163邮箱授权码

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // 明确启用 SSL

        // *** 这就是解决问题的关键！***
        // 强制使用 TLSv1.2 协议，避免因 JDK 版本低导致的握手失败
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 可以选择性添加超时设置，让程序更健壮
        props.put("mail.smtp.connectiontimeout", 5000); // 连接超时时间
        props.put("mail.smtp.timeout", 5000); // 读写超时时间

        return mailSender;
    }
}

