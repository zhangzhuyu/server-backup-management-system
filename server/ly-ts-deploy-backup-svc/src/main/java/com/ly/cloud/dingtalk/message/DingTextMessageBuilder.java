package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;

import java.util.List;

/**
 * Class Name: DingTextMsgBuilder Description: 封装【text】消息类型
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:31
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingTextMessageBuilder extends BaseMessageBuilder {

    private final OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
    private final OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();

    public DingTextMessageBuilder() {
        // 消息类型，此时固定为：text
        this.request.setMsgtype("text");
        this.request.setText(text);
    }

    /**
     * 设置消息内容
     * @param content
     * @return DingTextMessageBuilder
     */
    public DingTextMessageBuilder setContent(String content){
        if (content != null && !content.isEmpty()){
            this.text.setContent(content);
        }
        return this;
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @return DingTextMessageBuilder
     */
    public DingTextMessageBuilder mat(List<String> mobileList){
        return atByMobile(mobileList,false);
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @return DingTextMessageBuilder
     */
    public DingTextMessageBuilder uat(List<String> userIdList){
        return atByUserId(userIdList,false);
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @param isAtAll 是否@所有人
     * @return DingTextMessageBuilder
     */
    public DingTextMessageBuilder atByMobile(List<String> mobileList, boolean isAtAll){
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
     * @return DingTextMessageBuilder
     */
    public DingTextMessageBuilder atByUserId(List<String> userIdList, boolean isAtAll){
        if (userIdList != null && !userIdList.isEmpty()) {
            at.setAtUserIds(userIdList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }

    @Override
    public OapiRobotSendRequest build() {
        this.request.setText(text);
        this.request.setAt(at);
        return request;
    }

}
