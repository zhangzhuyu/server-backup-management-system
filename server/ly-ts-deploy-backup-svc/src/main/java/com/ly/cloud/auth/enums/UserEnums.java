package com.ly.cloud.auth.enums;

/**
 * 用户公共枚举类
 *
 * @author: wzz
 */
public enum UserEnums {

    /**
     * 性别 (0男 1女 2未知)
     */
    USER_SEX_MAN("0", "男"),
    USER_SEX_WOMAN("1", "女"),
    USER_SEX_UNKNOWN("2", "未知")
    ;

    private String code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getValue(String code) {
        for (UserEnums u : UserEnums.values()) {
            if (u.getCode().equals(code)) {
                return u.value;
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
    public static String getCode(String value) {
        for (UserEnums u : UserEnums.values()) {
            if (value.equals(u.getValue())) {
                return u.getCode();
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
    private UserEnums(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
