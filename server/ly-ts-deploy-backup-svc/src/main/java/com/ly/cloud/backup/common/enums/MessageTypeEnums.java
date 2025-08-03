package com.ly.cloud.backup.common.enums;

/**
 * 消息发送方式枚举类
 *
 * @author: SYC
 */
public enum MessageTypeEnums {

    /**
     * （钉钉:1、邮件:2、短信:3、企业微信:0）
     */
    ENTERPRISE_WECHAT(0, "enterpriseWeChat","企业微信"),
    DINGTALK(1, "dingTalk","钉钉"),
    MAIL(2, "mail","邮件"),
    TEXT_MESSAGE(3, "textMessage","短信")

    ;

    private Integer code;
    private String value;
    private String name;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getName(Integer code) {
        for (MessageTypeEnums c : MessageTypeEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.name;
            }
        }
        return null;
    }

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getValue(Integer code) {
        for (MessageTypeEnums c : MessageTypeEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.value;
            }
        }
        return null;
    }

    /**
     * 通过值获取代码
     *
     * @param value : 值
     * @return Integer : 代码
     */
    public static Integer getCode(String value) {
        for (MessageTypeEnums c : MessageTypeEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return null;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     */
    private MessageTypeEnums(Integer code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
