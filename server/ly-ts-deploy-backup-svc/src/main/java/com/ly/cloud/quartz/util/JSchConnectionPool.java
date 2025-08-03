package com.ly.cloud.quartz.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import java.util.Vector;

public class JSchConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(JSchConnectionPool.class);

    /**
     * 超时时间
     */
    private Integer timeout;

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

    /**
     * 每次扩容增加几个连接
     */
    private int incrementalConnections = 2;

    /**
     * 最大连接数
     */
    private int maxConnections = 10;

    private Vector<JSchPooledConnection> connections = null;


    public JSchConnectionPool() {
    }

    @PostConstruct
    private void init() {
        createPool();
    }

    public int getIncrementalConnections() {
        return this.incrementalConnections;

    }

    public void setIncrementalConnections(int incrementalConnections) {
        this.incrementalConnections = incrementalConnections;

    }

    public int getMaxConnections() {
        return this.maxConnections;

    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;

    }

    /**
     * 构造方法
     * @param timeout 超时时间
     */
    public JSchConnectionPool(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * 构造方法
     * @param timeout 超时时间
     * @param incrementalConnections 增量大小
     */
    public JSchConnectionPool(Integer timeout, int incrementalConnections) {
        this.timeout = timeout;
        this.incrementalConnections = incrementalConnections;
    }

    /**
     * 构造方法
     * @param timeout 超时时间
     * @param incrementalConnections 增量大小
     * @param maxConnections 连接池最大连接数
     */
    public JSchConnectionPool(Integer timeout, int incrementalConnections, int maxConnections) {
        this.timeout = timeout;
        this.incrementalConnections = incrementalConnections;
        this.maxConnections = maxConnections;
    }

    /**
     * 创建连接池，判断连接池是否创建，如果连接池没有创建则创建连接池
     */
    public synchronized void createPool() {
        if (Optional.ofNullable(connections).isPresent()) {
            return;
        }
        connections = new Vector<JSchPooledConnection>();
        log.info("create shell connectionPool success!");
    }

    /**
     * 创建指定数量的连接放入连接池中
     * @param numConnections 创建数量
     * @throws JSchException 建立远程连接异常
     */
    private void createConnections(int numConnections) throws JSchException {
        for (int x = 0; x < numConnections; x++) {
            // 判断是否已达连接池最大连接，如果到达最大连接数据则不再创建连接
            if (this.maxConnections > 0 && this.connections.size() >= this.maxConnections) {
                break;
            }
            //在连接池中新增一个连接
            try {
                connections.addElement(new JSchPooledConnection(newConnection(), ip));
            } catch (JSchException e) {
                log.error("create shell connection failed {}", e.getMessage());
                throw new JSchException();
            }
            log.info("Session connected!");
        }
    }

    /**
     * 新一个连接session
     * @return 创建的session
     * @throws JSchException 远程连接异常
     */
    private Session newConnection() throws JSchException {
        // 创建一个session
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, ip, port);
        session.setPassword(password);
        Properties sshConfig = new Properties();
        //去掉首次连接确认
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.connect(timeout);
        session.setServerAliveInterval(1800);
        return session;
    }

    /**
     * 获取一个可用session
     * @param ip ip地址
     * @param port 端口号
     * @param username 用户名
     * @param password 密码
     * @return 可用的session
     * @throws JSchException 远程连接异常
     */
    public synchronized Session getConnection(String ip, Integer port, String username, String password) throws JSchException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        // 连接池还没创建，则返回 null
        if (!Optional.ofNullable(connections).isPresent()) {
            return null;
        }
        // 获得一个可用的数据库连接
        Session session = getFreeConnection();
        // 假如目前没有可以使用的连接，即所有的连接都在使用中，等一会重试
        while (!Optional.ofNullable(session).isPresent()) {
            wait(250);
            session = getFreeConnection();
        }
        return session;
    }

    /**
     * 获取一个可用session
     * @return 返回可用session
     * @throws JSchException 远程连接异常
     */
    private Session getFreeConnection() throws JSchException {
        Session session = findFreeConnection();
        // 如果没有可用连接，则创建连接，
        if (!Optional.ofNullable(session).isPresent()) {
            createConnections(incrementalConnections);
            session = findFreeConnection();
            if (!Optional.ofNullable(session).isPresent()) {
                return null;
            }
        }
        return session;
    }

    /**
     * 查找可用连接
     * @return 返回可用连接
     */
    private Session findFreeConnection() {
        Session session = null;
        JSchPooledConnection conn;
        Enumeration<JSchPooledConnection> enumerate = connections.elements();
        // 遍历所有的对象，看是否有可用的连接
        while (enumerate.hasMoreElements()) {
            conn = enumerate.nextElement();
            if (!ip.equals(conn.getTag())) {
                continue;
            }
            if (!conn.isBusy()) {
                session = conn.getSession();
                conn.setBusy(true);
                if (!testConnection(session)) {
                    try {
                        session = newConnection();
                    } catch (JSchException e) {
                        log.error("create shell connection failed {}", e.getMessage());
                        return null;
                    }
                    conn.setSession(session);
                }
                break;
            }
        }
        return session;
    }

    /**
     * 测试连接是否可用
     * @param session 需要测试的session
     * @return 是否可用
     */
    private boolean testConnection(Session session) {
        boolean connected = session.isConnected();
        if (!connected) {
            closeConnection(session);
            return false;
        }
        return true;
    }

    /**
     * 将session放回连接池中
     * @param session 需要放回连接池中的session
     */
    public synchronized void returnConnection(Session session) {
        // 确保连接池存在，假如连接没有创建(不存在)，直接返回
        if (!Optional.ofNullable(connections).isPresent()) {
            log.error("ConnectionPool does not exist");
            return;
        }
        JSchPooledConnection conn;
        Enumeration<JSchPooledConnection> enumerate = connections.elements();
        // 遍历连接池中的所有连接，找到这个要返回的连接对象，将状态设置为空闲
        while (enumerate.hasMoreElements()) {
            conn = enumerate.nextElement();
            if (session.equals(conn.getSession())) {
                conn.setBusy(false);
            }
        }
    }

    /**
     * 刷新连接池
     * @throws JSchException 远程连接异常
     */
    public synchronized void refreshConnections() throws JSchException {
        // 确保连接池己创新存在
        if (!Optional.ofNullable(connections).isPresent()) {
            log.error("ConnectionPool does not exist");
            return;
        }
        JSchPooledConnection conn;
        Enumeration<JSchPooledConnection> enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            conn = enumerate.nextElement();
            if (conn.isBusy()) {
                wait(5000);

            }
            closeConnection(conn.getSession());
            conn.setSession(newConnection());
            conn.setBusy(false);
        }
    }

    /**
     * 关闭连接池
     */
    public synchronized void closeConnectionPool() {
        // 确保连接池存在，假如不存在，返回
        if (!Optional.ofNullable(connections).isPresent()) {
            log.info("Connection pool does not exist");
            return;
        }
        JSchPooledConnection conn;
        Enumeration<JSchPooledConnection> enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            conn = enumerate.nextElement();
            if (conn.isBusy()) {
                wait(5000);
            }
            closeConnection(conn.getSession());
            connections.removeElement(conn);
        }
        connections = null;
    }

    /**
     * 关闭session会话
     * @param session 需要关闭的session
     */
    private void closeConnection(Session session) {
        session.disconnect();
    }

    /**
     * 线程暂停
     * @param mSeconds 暂停时间
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        } catch (InterruptedException e) {
            log.error("{} 线程暂停失败 -> {}", Thread.currentThread().getName(), e.getMessage());
        }
    }
}
