package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.ly.cloud.dingtalk.component.DingFeedCardContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: DingFeedCardMsgBuilder Description: 封装【feedCard】消息类型
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:35
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingFeedCardMessageBuilder extends BaseMessageBuilder {

    private final OapiRobotSendRequest.Feedcard feedcard = new OapiRobotSendRequest.Feedcard();
    private final OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
    
    public DingFeedCardMessageBuilder() {
        // 消息类型，此时固定为：feedCard
        this.request.setMsgtype("feedCard");
        this.request.setFeedCard(feedcard);
    }

    /**
     * @Title: setFeedCardContent
     * @Description: 设置feedCard卡片组
     * @author: jiangzhongxin
     * @date: 2022/1/6 17:10
     * @param contentList: 内容集合
     * @return: DingFeedCardMessageBuilder
     */
    public DingFeedCardMessageBuilder setFeedCardContent(List<DingFeedCardContent> contentList){
        if (contentList != null && !contentList.isEmpty()){
            List<OapiRobotSendRequest.Links> links = new ArrayList<>(contentList.size());
            contentList.forEach(content ->{
                OapiRobotSendRequest.Links tempLink = new OapiRobotSendRequest.Links();
                // 单条信息后面图片的URL
                tempLink.setPicURL(content.getPicUrl());
                // 点击单条信息到跳转链接（PC端跳转目标页面的方式，请参考消息链接在PC端侧边栏或者外部浏览器打开。）
                tempLink.setMessageURL(content.getMessageUrl());
                // 单条信息文本
                tempLink.setTitle(content.getTitle());
                links.add(tempLink);
            });
            feedcard.setLinks(links);
        }
        return this;
    }
    
    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @return DingFeedCardMessageBuilder
     */
    public DingFeedCardMessageBuilder mat(List<String> mobileList){
        return atByMobile(mobileList,false);
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @return DingFeedCardMessageBuilder
     */
    public DingFeedCardMessageBuilder uat(List<String> userIdList){
        return atByUserId(userIdList,false);
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @param isAtAll 是否@所有人
     * @return DingFeedCardMessageBuilder
     */
    public DingFeedCardMessageBuilder atByMobile(List<String> mobileList, boolean isAtAll){
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
     * @return DingFeedCardMessageBuilder
     */
    public DingFeedCardMessageBuilder atByUserId(List<String> userIdList, boolean isAtAll){
        if (userIdList != null && !userIdList.isEmpty()) {
            at.setAtUserIds(userIdList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }
    
    @Override
    public OapiRobotSendRequest build() {
        this.request.setFeedCard(feedcard);
        this.request.setAt(at);
        return request;
    }

}
