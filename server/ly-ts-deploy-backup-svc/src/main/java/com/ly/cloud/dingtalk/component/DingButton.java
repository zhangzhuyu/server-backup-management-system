package com.ly.cloud.dingtalk.component;

import java.io.Serializable;

/**
 * Class Name: DingButton Description: 钉钉消息内的button
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:26
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class DingButton implements Serializable {

    /**
     * 按钮名称
     */
    private String title;

    /**
     * 按钮访问路由
     */
    private String url;

    public DingButton(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}