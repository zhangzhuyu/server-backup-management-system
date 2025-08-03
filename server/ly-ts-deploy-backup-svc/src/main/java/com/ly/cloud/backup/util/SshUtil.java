package com.ly.cloud.backup.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author shaoyangliu
 * @date 创建时间：2022年03月14日 09:37:35
 * @description ssh工具类
 */
public class SshUtil {

    private static Logger logger = LoggerFactory.getLogger(SshUtil.class);

    private static String DEFAULT_CHAR_SET = "UTF-8";

    /**
     * 登录主机
     * @return
     *      登录成功返回true，否则返回false
     */
    public static Connection login(String ip, int port, String userName, String password){
        Connection conn = null;
        try {
            conn = new Connection(ip, port);
            conn.connect(); // 连接主机
            boolean isAuthenticated = conn.authenticateWithPassword(userName, password); // 认证
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return conn;
    }

    /**
     * 远程执行shell脚本或者命令
     * @param cmd
     *      即将执行的命令
     * @return
     *      命令执行完后返回的结果值
     */
    public static String execute(Connection conn, String cmd) throws IOException {
        String result = "";
        Session session = null;
        try {
            if(conn != null) {
                try {
                    session = conn.openSession();  // 打开一个会话
                } catch (IllegalStateException e) {
                    logger.error(e.getMessage(), e);
                }
                if (session !=null){
                   session.execCommand(cmd);      // 执行命令
                   result = processStdout(session.getStdout(), DEFAULT_CHAR_SET);
                   //如果为得到标准输出为空，说明脚本执行出错了
                   if(StringUtils.isBlank(result)){
                       logger.error("命令" + cmd + "输出结果为空");
                   }
               }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (session != null) {
                session.close();
            }
        }
        return result;
    }

    /**
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @param charset 编码
     * @return
     *       以纯文本的格式返回
     */
    private static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line;
            while((line = br.readLine()) != null){
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return buffer.toString();
    }

}