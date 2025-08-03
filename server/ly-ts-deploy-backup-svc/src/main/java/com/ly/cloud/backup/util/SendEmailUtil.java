package com.ly.cloud.backup.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * @author SYC
 * @date 创建时间：2022年03月07日 09:37:35
 * @description 发送邮件工具类
 */
@Component
public class SendEmailUtil {

    //@Value("${formMail:lydsc@ly-sky.com}")
    @Value("${formMail:lydsc@ly-sky.com}")
    private String formMailUser;

    //@Value("${formMailPassword:TXQIYXlianyi123}")
    @Value("${formMailPassword:TXQYIXlianyi123}")
    private String formMailPassword;

    @Value("${mail.smtp.host:smtp.qq.com}")
    private String HOST;

    @Value("${mail.smtp.port:465}")
    private String PORT;

    @Value("${mail.smtp.auth:true}")
    private String AUTH;

    @Value("${mail.smtp.ssl.enable:true}")
    private String SSL_ENABLE;

    @Value("${mail.debug:true}")
    private String DEBUG;

    @Value("${mail.transport.protocol:smtp}")
    private String TRANSPORT_PROTOCOL;

    @Value("${mail.smtpPassword:tstejgwukbkbbehf}")
    private String smtpPassword;


    public static void sendMail(String addressee, String title, String content,String path,
                                String formMailUser, String formMailPassword) throws AddressException, MessagingException, UnsupportedEncodingException {
        // 创建程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.exmail.qq.com");
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true
        props.put("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        // 创建验证器
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(formMailUser, formMailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);
        // 创建Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(formMailUser)); // 设置发送者
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(addressee)); // 设置发送方式与接收者
        //邮件-标题
        message.setSubject(title);
        message.setContent(content, "text/html;charset=utf-8");
                // 设置邮件正文:这是含有附件的邮件
                Multipart multipart = new MimeMultipart();
                // 添加text:
                BodyPart textpart = new MimeBodyPart();
                textpart.setContent(content, "text/html;charset=utf-8");
                multipart.addBodyPart(textpart);
                //发送邮件，发送本地文件
                BodyPart filepart = new MimeBodyPart();
                String contentType = "text/html;charset=UTF-8";
                filepart.setHeader(contentType, contentType);
//                FileDataSource fileDataSource = new FileDataSource(path);
//                filepart.setDataHandler(new DataHandler(fileDataSource));
//                // 处理附件名称中文（附带文件路径）乱码问题
//                filepart.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
//                multipart.addBodyPart(filepart);
                // 设置邮件内容为multipart:
                message.setContent(multipart);

        //创建Transport用于将邮件发送
        Transport.send(message);


    }

    public  void sendWarningMail(List<String> address, String title, String content) throws AddressException, MessagingException, UnsupportedEncodingException {

        // 创建程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.exmail.qq.com");
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true
        props.put("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.reportsuccess","true");
        // 创建验证器
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(formMailUser, formMailPassword);
            }
        };
        Session session = Session.getInstance(props, auth);
        // 创建Message，它相当于是邮件内容
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(formMailUser)); // 设置发送者
        // 多个收件人地址
        InternetAddress[] addressesTo = null;
        int count = address.size();
            if (count > 0) {
                addressesTo = new InternetAddress[count];
                for (int i = 0; i < count; i++) {
                    addressesTo[i] = new InternetAddress(address.get(i));
                }
            }
        message.setRecipients(MimeMessage.RecipientType.TO, addressesTo); // 设置发送方式与接收者
        //邮件-标题
        message.setSubject(title);
        message.setContent(content, "text/html;charset=utf-8");
        // 设置邮件正文:这是含有附件的邮件
        Multipart multipart = new MimeMultipart();
        // 添加text:
        BodyPart textpart = new MimeBodyPart();
        textpart.setContent(content, "text/html;charset=utf-8");
        multipart.addBodyPart(textpart);
        //发送邮件，发送本地文件
        BodyPart filepart = new MimeBodyPart();
        String contentType = "text/html;charset=UTF-8";
        filepart.setHeader(contentType, contentType);
//                FileDataSource fileDataSource = new FileDataSource(path);
//                filepart.setDataHandler(new DataHandler(fileDataSource));
//                // 处理附件名称中文（附带文件路径）乱码问题
//                filepart.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
//                multipart.addBodyPart(filepart);
        // 设置邮件内容为multipart:
        message.setContent(multipart);

        //创建Transport用于将邮件发送
        Transport.send(message);

    }

    public static void main(String[] args) throws MessagingException, UnsupportedEncodingException {

//      发件人邮箱账号
        String formMailUser = "lydsc@ly-sky.com";
//        发件人密码
        String formMailPassword = "TXQIYXlianyi123";
//        收件人邮箱账号
        String addressee = "lijinbiao@ly-sky.com";
//        标题
        String title = "我是标题党";
//        内容
        String content = "【运维监控平台】[主机][192.169.2.1][重要告警]尊敬的gjkfdx2018，磁盘I/O使用率连续3次原始值>= 90.00%，当前数据:92.47%，于2022/06/1512:50:15GMT+08:00触发告警，触发规则名字:JW-ORACLE-A2-mount_point，详情登录运维监控平台查看";
        String path = "";
//        附件
//        File[] files = new File[2];
//        files[0] = new File(path);
        //发送邮件
        sendMail(addressee, title, content,  path, formMailUser, formMailPassword);
    }
}

