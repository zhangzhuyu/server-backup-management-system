package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;

import java.util.List;

/**
 * Class Name: DingMarkDownMsgBuilder Description: 封装【markdown】消息类型
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:33
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingMarkDownMessageBuilder extends BaseMessageBuilder {

    private final OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
    private final OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();

    public DingMarkDownMessageBuilder() {
        // 消息类型，此时固定为：markdown
        this.request.setMsgtype("markdown");
        this.request.setMarkdown(markdown);
    }

    /**
     * 设置首屏会话透出的展示内容
     * @param title 首屏会话透出的展示内容
     * @return DingLinkMessageBuilder
     */
    public DingMarkDownMessageBuilder setMarkdownTitle(String title){
        if (title != null && !title.isEmpty()){
            this.markdown.setTitle(title);
        }
        return this;
    }

    /**
     * 设置消息内容
     * @param content markdown格式的消息
     * @return DingLinkMessageBuilder
     */
    public DingMarkDownMessageBuilder setMarkdownContent(String content){
        if (content != null && !content.isEmpty()){
            this.markdown.setText(content);
        }
        return this;
    }


    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @return DingMarkDownMessageBuilder
     */
    public DingMarkDownMessageBuilder mat(List<String> mobileList){
        return atByMobile(mobileList,false);
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @return DingMarkDownMessageBuilder
     */
    public DingMarkDownMessageBuilder uat(List<String> userIdList){
        return atByUserId(userIdList,false);
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @param isAtAll 是否@所有人
     * @return DingMarkDownMessageBuilder
     */
    public DingMarkDownMessageBuilder atByMobile(List<String> mobileList, boolean isAtAll){
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
     * @return DingMarkDownMessageBuilder
     */
    public DingMarkDownMessageBuilder atByUserId(List<String> userIdList, boolean isAtAll){
        if (userIdList != null && !userIdList.isEmpty()) {
            at.setAtUserIds(userIdList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }

    @Override
    public OapiRobotSendRequest build() {
        this.request.setMarkdown(markdown);
        this.request.setAt(at);
        return request;
    }

}
