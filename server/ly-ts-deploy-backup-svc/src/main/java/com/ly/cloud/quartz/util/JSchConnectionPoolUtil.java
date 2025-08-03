package com.ly.cloud.quartz.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Scope("prototype")
public class JSchConnectionPoolUtil {

    @Autowired
    private JSchConnectionPool jSchConnectionPool;

    private static final Logger log = LoggerFactory.getLogger(JSchConnectionPoolUtil.class);
    /**
     * ip地址
     */
    private String ip = "";

    /**
     * 端口号
     */
    private Integer port = 22;

    /**
     * 用户名
     */
    private String username = "";

    /**
     * 密码
     */
    private String password = "";

    private Session session;

    private Channel channel;

    private ChannelExec channelExec;

    private ChannelSftp channelSftp;

    private ChannelShell channelShell;

    /**
     * 初始化
     * @param ip 远程主机IP地址
     * @param port 远程主机端口
     * @param username 远程主机登陆用户名
     * @param password 远程主机登陆密码
     * @throws JSchException JSch异常
     */
    public void init(String ip, Integer port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void init(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    private void getSession() throws JSchException {
        session = jSchConnectionPool.getConnection(ip, port, username, password);
        if (!Optional.ofNullable(session).isPresent()) {
            jSchConnectionPool.refreshConnections();
            session = jSchConnectionPool.getConnection(ip, port, username, password);
            if (!Optional.ofNullable(session).isPresent()){
                throw new RuntimeException("无可用连接");
            }
        }
    }

    /**
     * 连接多次执行命令，执行命令完毕后需要执行close()方法
     * @param command 需要执行的指令
     * @return 执行结果
     * @throws Exception 没有执行初始化
     */
    public String execCmd(String command) throws Exception {
        initChannelExec();
        log.info("execCmd command - > {}", command);
        channelExec.setCommand(command);
        channel.setInputStream(null);
        channelExec.setErrStream(System.err);
        channel.connect();
        StringBuilder sb = new StringBuilder(16);
        try (InputStream in = channelExec.getInputStream();
             InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
                String buffer;
                while ((buffer = reader.readLine()) != null) {
                    sb.append("\n").append(buffer);
                }
                log.info("execCmd result - > {}", sb);
                return sb.toString();
             }
    }

    /**
     * 执行命令归还session到连接池
     * @param command 需要执行的指令
     * @return 执行结果
     * @throws Exception 没有执行初始化
     */
    public String execCmdAndClose(String command) throws Exception {
        String result = execCmd(command);
        close();
        return result;
    }

    /**
     * 释放资源
     */
    public void close() {
        jSchConnectionPool.returnConnection(session);
    }

    private void initChannelSftp() throws Exception {
        getSession();
        channel = session.openChannel("sftp");
        channel.connect(); // 建立SFTP通道的连接
        channelSftp = (ChannelSftp) channel;
        if (session == null || channel == null || channelSftp == null) {
            log.error("请先执行init()");
            throw new Exception("请先执行init()");
        }
    }

    private void initChannelExec() throws Exception {
        getSession();
        // 打开执行shell指令的通道
        channel = session.openChannel("exec");
        channelExec = (ChannelExec) channel;
        if (session == null || channel == null || channelExec == null) {
            log.error("请先执行init()");
            throw new Exception("请先执行init()");
        }
    }

    private void initChannelShell() throws Exception {
        getSession();
        // 打开执行shell指令的通道
        channel = session.openChannel("shell");
        channelShell = (ChannelShell) channel;
        if (session == null || channel == null || channelShell == null) {
            log.error("请先执行init()");
            throw new Exception("请先执行init()");
        }
    }
}
