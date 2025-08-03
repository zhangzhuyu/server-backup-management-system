package com.ly.cloud.quartz.util;

import com.jcraft.jsch.Session;

public class JSchPooledConnection {

    Session session;
    /**
     * 此连接是否正在使用的标志，默认没有正在使用
     */
    boolean busy = false;
    /**
     * 连接标记
     */
    String tag;

    /**
     * 构造函数，根据一个 Session 构造一个 PooledConnection 对象
     *
     * @param session 连接
     * @param tag 连接标记
     */

    public JSchPooledConnection(Session session, String tag) {
        this.session = session;
        this.tag = tag;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
