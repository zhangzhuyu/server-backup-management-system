package com.ly.cloud.backup.common.enums;

/**
 * 指标（告警）类型公共枚举类
 *
 * @author: SYC
 */
public enum TargetTypeEnums {

    /**
     * 告警等级
     */
    SERVICE_STATUS(1, "服务状态"),
    CPU_RATE(2, "CPU使用率"),
    RAM_RATE(3, "内存使用率"),
    SYSTEM_DISK_RATE(4, "系统盘使用率"),
    APPLICATION_HEALTH(5, "应用健康状态"),
    OTHER(9, "其他"),
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
        for (TargetTypeEnums c : TargetTypeEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.value;
            }else{
                return String.valueOf(code);
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
        for (TargetTypeEnums c : TargetTypeEnums.values()) {
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
    private TargetTypeEnums(Integer code, String value) {
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
