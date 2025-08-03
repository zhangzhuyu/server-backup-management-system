package com.ly.cloud.backup.common.enums;


import java.util.Calendar;


/**
 * 时间类型枚举类
 *
 * @author: SYC
 */
public enum CalendarEnums {

    /**
     * 按分钟计算
     */
    CALENDAR_ENUMS_MINUTE_ONE("1", Calendar.MINUTE),

    /**
     * 按分钟计算
     */
    CALENDAR_ENUMS_MINUTE_TWO("2", Calendar.MINUTE),

    /**
     * 按分钟计算
     */
    CALENDAR_ENUMS_MINUTE_THREE("3", Calendar.MINUTE),


    /**
     * 按小时计算
     */
    CALENDAR_ENUMS_HOUR("4", Calendar.HOUR),

    /**
     * 按天计算
     */
    CALENDAR_ENUMS_DAY("5", Calendar.DATE),

    /**
     * 按每月的天数计算
     */
    CALENDAR_ENUMS_DAY_OF_MONTH("6", Calendar.DAY_OF_MONTH),

    /**
     * 按月计算
     */
    CALENDAR_ENUMS_MONTH("7", Calendar.MONTH),

    /**
     * 自定义选择时间
     */
    CALENDAR_ENUMS_CUSTOM_TIME("8",8),



    ;

    private String code;
    private int value;


    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static int getValue(String code) {
        for (CalendarEnums c : CalendarEnums.values()) {
            if (c.getCode().equalsIgnoreCase(code)) {
                return c.value;
            }
        }
        return 0;
    }

    /**
     * 通过值获取代码
     *
     * @param value : 值
     * @return Integer : 代码
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getCode(int value) {
        for (CalendarEnums c : CalendarEnums.values()) {
            if (value==c.getValue()) {
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
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private CalendarEnums(String code, int value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
