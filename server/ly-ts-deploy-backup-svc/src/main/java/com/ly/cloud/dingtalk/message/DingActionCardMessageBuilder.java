package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.ly.cloud.dingtalk.component.DingButton;
import com.ly.cloud.dingtalk.component.DingCommonEnums;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: DingActionCardMsgBuilder Description: 封装【actionCard】消息类型：整体跳转ActionCard类型
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:37
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingActionCardMessageBuilder extends BaseMessageBuilder {

    private final OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
    private final OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
    
    public DingActionCardMessageBuilder() {
        // 消息类型，此时固定为：actionCard
        this.request.setMsgtype("actionCard");
        this.request.setActionCard(actionCard);
    }

    /**
     * 设置首屏会话透出的展示内容
     * @param title
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            this.actionCard.setTitle(title);
        }
        return this;
    }

    /**
     * 设置内容（markdown格式的消息）
     * @param markDownContent
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setContent(String markDownContent) {
        if (markDownContent != null && !markDownContent.isEmpty()) {
            this.actionCard.setText(markDownContent);
        }
        return this;
    }

    /**
     * 设置按钮的排列方向{ 0：按钮竖直排列；1：按钮横向排列 }
     * @param orientation
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setBtnOrientation(DingCommonEnums orientation) {
        this.actionCard.setBtnOrientation(orientation.getCode().toString());
        return this;
    }

    /**
     * 设置hideAvatar
     * @param isHideAvatar
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setHideAvatar(boolean isHideAvatar) {
        this.actionCard.setHideAvatar(isHideAvatar ? "1" : "0");
        return this;
    }

    /**
     * @Title: setBtns
     * @Description: 设置按钮组，注意：如果设置了【singleBtn】则【setBtns】会无效
     * @author: jiangzhongxin
     * @date: 2022/1/6 16:42
     * @param buttonList 按钮组
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setBtns(List<DingButton> buttonList) {
        if (buttonList != null && !buttonList.isEmpty()) {
            if (buttonList.size() == 1) {
                return setSingleBtn(buttonList.get(0));
            }
            List<OapiRobotSendRequest.Btns> toBtns = new ArrayList<>(buttonList.size());
            buttonList.forEach(btn -> {
                OapiRobotSendRequest.Btns tempBtn = new OapiRobotSendRequest.Btns();
                tempBtn.setActionURL(btn.getUrl());
                tempBtn.setTitle(btn.getTitle());
                toBtns.add(tempBtn);
            });
            this.actionCard.setBtns(toBtns);
        }
        return this;
    }

    /**
     * @Title: setSingleBtn
     * @Description: 设置单个按钮的标题及点击消息跳转的URL：注意 设置此项后，setBtns无效。
     * @author: jiangzhongxin
     * @date: 2022/1/6 16:58
     * @param dingButton:
     * @return: DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder setSingleBtn(DingButton dingButton) {
        if (dingButton != null) {
            this.actionCard.setSingleTitle(dingButton.getTitle());
            this.actionCard.setSingleURL(dingButton.getUrl());
        }
        return this;
    }
    
    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder mat(List<String> mobileList){
        return atByMobile(mobileList,false);
    }

    /**
     * 设置消息@对象
     * @param userIdList 被@人的id
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder uat(List<String> userIdList){
        return atByUserId(userIdList,false);
    }

    /**
     * 设置消息@对象
     * @param mobileList 被@人的手机号（注意 在content里添加@人的手机号，且只有在群内的成员才可被@，非群内成员手机号会被脱敏。）
     * @param isAtAll 是否@所有人
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder atByMobile(List<String> mobileList, boolean isAtAll){
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
     * @return DingActionCardMsgBuilder
     */
    public DingActionCardMessageBuilder atByUserId(List<String> userIdList, boolean isAtAll){
        if (userIdList != null && !userIdList.isEmpty()) {
            at.setAtUserIds(userIdList);
        }
        at.setIsAtAll(isAtAll);
        return this;
    }

    @Override
    public OapiRobotSendRequest build() {
        this.request.setActionCard(actionCard);
        this.request.setAt(at);
        return request;
    }

}
