package com.ly.cloud.dingtalk.service;

import com.ly.cloud.dingtalk.dto.ReceivedMessage;

/**
 * @author admin
 */
public interface DingService {

    /**
     * 根据关键字发送消息
     *
     * @param keyword         : 消息关键字
     * @param receivedMessage : 接受的消息数据
     * @author: jiangzhongxin
     * @date: 2022/3/7 15:42
     */
    void sendMessage(String keyword, ReceivedMessage receivedMessage);

}
