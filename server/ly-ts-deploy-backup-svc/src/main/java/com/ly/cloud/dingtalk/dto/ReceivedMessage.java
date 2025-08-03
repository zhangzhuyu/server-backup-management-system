package com.ly.cloud.dingtalk.dto;

import com.dingtalk.api.DingTalkClient;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class Name: ReceivedMessage Description: 文档地址：https://open.dingtalk.com/document/group/enterprise-created-chatbot
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年03月07日 16:29
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Data
public class ReceivedMessage implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -537575900609656909L;

    /**
     * 目前只支持text
     */
    private String msgtype;

    /**
     * 消息文本
     */
    private LinkedHashMap<String, String> text;

    /**
     * 消息文本
     */
    private String content;

    /**
     * 加密的消息ID
     */
    private String msgId;

    /**
     * 消息的时间戳，单位ms
     */
    private String createAt;

    /**
     * 1-单聊、2-群聊
     */
    private String conversationType;

    /**
     * 加密的会话ID 群号cid
     */
    private String conversationId;

    /**
     * 会话标题(群聊时才有)
     */
    private String conversationTitle;

    /**
     * 加密的发送者ID
     */
    private String senderId;

    /**
     * 发送者昵称
     */
    private String senderNick;

    /**
     * 是否管理员
     */
    private Boolean isAdmin;

    /**
     * 被@人的信息dingtalkId: 加密的发送者IDstaffId: 发送者在企业内的userid(企业内部群有)
     */
    private List<LinkedHashMap<String, String>> atUsers;

    /**
     * 是否在@列表中
     */
    private Boolean isInAtList;

    /**
     * 发送者当前群的企业corpId(企业内部群有)
     */
    private String senderCorpId;

    /**
     * 发送者在企业内的userid(企业内部群有) 用户userId
     */
    private String senderStaffId;

    /**
     * 加密的机器人ID
     */
    private String chatbotUserId;

    /**
     * 加密的机器人所在的企业corpId
     */
    private String chatbotCorpId;

    /**
     * 当前会话的Webhook地址
     */
    private String sessionWebhook;

    /**
     * 当前会话的Webhook地址过期时间。
     */
    private String sessionWebhookExpiredTime;

    /**
     * 客户端
     */
    private DingTalkClient client;

}
