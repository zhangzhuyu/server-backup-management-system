package com.ly.cloud.dingtalk.constant;

/**
 * 钉钉开放接口网关常量
 *
 * @author: jiangzhongxin
 */
public class UrlConstant {

    /**
     * 获取access_token url
     */
    public static final String GET_ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";

    /**
     * 获取robot_send url
     */
    public static final String ROBOT_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * 通过免登授权码获取用户信息 url
     */
    public static final String GET_USER_INFO_URL = "https://oapi.dingtalk.com/topapi/v2/user/getuserinfo";

    /**
     * 根据用户id获取用户详情 url
     */
    public static final String USER_GET_URL = "https://oapi.dingtalk.com/topapi/v2/user/get";

    /**
     * 发送群助手消息 url
     */
    public static final String SCENCEGROUP_MESSAGE_SEND_V2 = "https://oapi.dingtalk.com/topapi/im/chat/scencegroup/message/send_v2";

    /**
     * 钉钉请求地址-注册互动卡片回调地址
     */
    public static final String INTERACTIVE_CARD_REGISTER_CALLBACK_URL = "https://oapi.dingtalk.com/topapi/im/chat/scencegroup/interactivecard/callback/register";

    /**
     * 通过手机号获取用户id
     */
    public static final String GET_USER_INFO_BY_MOBILE_URL = "https://oapi.dingtalk.com/topapi/v2/user/getbymobile";

    /**
     * 上传媒体文件
     */
    public static final String MEDIA_UPLOAD = "https://oapi.dingtalk.com/media/upload";

}
