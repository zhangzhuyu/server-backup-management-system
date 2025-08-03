package com.ly.cloud.dingtalk.component;

import java.io.Serializable;

/**
 * Class Name: DingFeedCardContent Description: FeedCard类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:27
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingFeedCardContent implements Serializable {

    /**
     * 卡片title
     */
    private String title;

    /**
     * 消息地址
     */
    private String messageUrl;

    /**
     * 图片地址
     */
    private String picUrl;

    public DingFeedCardContent(String title, String messageUrl, String picUrl) {
        this.title = title;
        this.messageUrl = messageUrl;
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

}
