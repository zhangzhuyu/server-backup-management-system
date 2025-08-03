package com.ly.cloud.backup.common.enums;

/**
 * 巡检报告枚举类
 *
 * @author: SYC
 */
public enum InspectionStrategyEnums {

    /**
     * 是否启用 0 禁用 1 启用
     */
    PDF(0, "pdf"),
    DOC(1, "doc"),
    DOCX(2, "docx");

    private Integer code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getValue(Integer code) {
        for (InspectionStrategyEnums c : InspectionStrategyEnums.values()) {
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
        for (InspectionStrategyEnums c : InspectionStrategyEnums.values()) {
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
    private InspectionStrategyEnums(Integer code, String value) {
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
