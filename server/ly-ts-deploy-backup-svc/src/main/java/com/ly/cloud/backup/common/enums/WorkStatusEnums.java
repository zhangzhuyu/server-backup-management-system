package com.ly.cloud.backup.common.enums;

/**
 * 处理状态公共枚举类
 *
 * @author: SYC
 */
public enum WorkStatusEnums {

    /**
     * 处理状态
     */
    PENDING(-1, "待处理"),
    PROCESSING(0, "正在处理"),
    RESOLVED(2, "已处理并解决"),
    UNSOLVED(3, "已处理未解决"),
    IGNORE(1, "忽略"),
    ;

    private Integer code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getValue(Integer code) {
        for (WorkStatusEnums c : WorkStatusEnums.values()) {
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
        for (WorkStatusEnums c : WorkStatusEnums.values()) {
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
    private WorkStatusEnums(Integer code, String value) {
        this.code = code;
        this.value = value;
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

}
