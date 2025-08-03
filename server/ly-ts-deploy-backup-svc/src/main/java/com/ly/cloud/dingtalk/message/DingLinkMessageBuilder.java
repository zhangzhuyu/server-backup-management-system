package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;

import java.util.List;

/**
 * Class Name: DingLinkMsgBuilder Description: 封装【link】消息类型
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:35
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingLinkMessageBuilder extends BaseMessageBuilder {

    private final OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
    private final OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
    
    public DingLinkMessageBuilder() {
        // 消息类型，此时固定为：link
        this.request.setMsgtype("link");
        this.request.setLink(link);
    }

    /**
     * 设置消息标题
     * @param title 标题
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder setTitle(String title){
        if (title != null && !title.isEmpty()){
            this.link.setTitle(title);
        }
        return this;
    }

    /**
     * 设置点击消息跳转的URL（打开方式如下：移动端，在钉钉客户端内打开；PC端：默认侧边栏打开、希望在外部浏览器打开，请参考消息链接说明）
     * @param link 点击消息跳转的URL
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder setLink(String link){
        if (link != null && !link.isEmpty()){
            this.link.setMessageUrl(link);
        }
        return this;
    }

    /**
     * 设置图片URL
     * @param picUrl 图片URL
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder setPic(String picUrl){
        if (picUrl != null && !picUrl.isEmpty()){
            this.link.setPicUrl(picUrl);
        }
        return this;
    }

    /**
     * 设置消息内容
     * @param text 消息内容。如果太长只会部分展示
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder setText(String text){
        if (text != null && !text.isEmpty()){
            this.link.setText(text);
        }
        return this;
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder mat(List<String> mobileList){
        return atByMobile(mobileList,false);
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder uat(List<String> userIdList){
        return atByUserId(userIdList,false);
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @param isAtAll 是否@所有人
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder atByMobile(List<String> mobileList, boolean isAtAll){
        if (mobileList != null && !mobileList.isEmpty()) {
            at.setAtMobiles(mobileList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @param isAtAll 是否@所有人
     * @return DingLinkMessageBuilder
     */
    public DingLinkMessageBuilder atByUserId(List<String> userIdList, boolean isAtAll){
        if (userIdList != null && !userIdList.isEmpty()) {
            at.setAtUserIds(userIdList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }

    @Override
    public OapiRobotSendRequest build() {
        this.request.setLink(link);
        this.request.setAt(at);
        return request;
    }

}
