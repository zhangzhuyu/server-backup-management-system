package com.ly.cloud.backup.common.enums;

/**
 * 告警等级公共枚举类
 *
 * @author: SYC
 */
public enum AlarmLevelEnums {

    /**
     * 告警等级
     */
    ALARM_LEVEL_COMMONLY(1, "一般"),
    ALARM_LEVEL_WARNING(2, "警告"),
    ALARM_LEVEL_SERIOUS(3, "严重"),
    ALARM_LEVEL_URGENT(4, "紧急"),
    ;

    private Integer code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (AlarmLevelEnums c : AlarmLevelEnums.values()) {
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
     * @date 2022/3/23 13:41
     */
    public static Integer getCode(String value) {
        for (AlarmLevelEnums c : AlarmLevelEnums.values()) {
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
     * @date 2022/3/23 13:42
     */
    private AlarmLevelEnums(Integer code, String value) {
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
