package com.ly.cloud.auth.service;

import com.ly.cloud.auth.enums.UserEnums;

/**
 * 用户状态
 * 
 *
 */
public enum UserStatus
{
    OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除");

    private String code;
    private String info;

    UserStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getInfo(String code) {
        for (UserStatus u : UserStatus.values()) {
            if (u.getCode().equals(code)) {
                return u.info;
            }
        }
        return null;
    }

    /**
     * 通过值获取代码
     *
     * @param info : 值
     * @return Integer : 代码
     */
    public static String getCode(String info) {
        for (UserStatus u : UserStatus.values()) {
            if (info.equals(u.getInfo())) {
                return u.getCode();
            }
        }
        return null;
    }
}
