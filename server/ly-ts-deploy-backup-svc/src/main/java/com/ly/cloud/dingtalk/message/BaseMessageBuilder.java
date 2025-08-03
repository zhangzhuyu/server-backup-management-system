package com.ly.cloud.dingtalk.message;

import com.dingtalk.api.request.OapiRobotSendRequest;

/**
 * Class Name: DingMessageBuilder Description: 钉钉消息封装的父类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:31
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public abstract class BaseMessageBuilder {

    public final OapiRobotSendRequest request = new OapiRobotSendRequest();

    /**
     * build OapiRobotSendRequest
     * @return OapiRobotSendRequest
     */
    public abstract OapiRobotSendRequest build();

}
