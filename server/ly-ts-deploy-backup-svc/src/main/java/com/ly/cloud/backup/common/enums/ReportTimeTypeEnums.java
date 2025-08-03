package com.ly.cloud.backup.common.enums;

/**
 * 运维报告时间类型枚举类
 *
 * @author: SYC
 */
public enum ReportTimeTypeEnums {

    /**
     * 时间类型
     */
    WEEK(0, "周"),
    MONTH(1, "月"),
    YEAR(2, "年"),
    CUSTOM(3, "自定义时间段"),
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
        for (ReportTimeTypeEnums c : ReportTimeTypeEnums.values()) {
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
        for (ReportTimeTypeEnums c : ReportTimeTypeEnums.values()) {
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
    private ReportTimeTypeEnums(Integer code, String value) {
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
