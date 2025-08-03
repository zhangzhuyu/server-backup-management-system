package com.ly.cloud.backup.common.enums;

import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;

/**
 * 时间枚举类
 *
 * @author: SYC
 */
public enum TimeEnums {

    /**
     * 时间类型
     */
    //按分钟计算
    FIFTEEN_MINUTE(1, 15.0,MINUTE,1.0),
    //按分钟计算
    THIRTY_MINUTE(2, 30.0,MINUTE,1.0),
    //按分钟计算
    ONE_HOUR(3, 1*60.0,HOUR,1.0),
    //天按小时计算(按照apm的标准，天按每10分钟1个点)
    TWENTY_FOUR_HOUR(4, 24*60.0,HOUR,10.0),
//    TWENTY_FOUR_HOUR(4, 24*60.0,HOUR,60.0),
    //周按天计算(按照apm的标准，周按小时)
    ONE_WEEK(5, 7*24*60.0,DAY,60.0),
//    ONE_WEEK(5, 7*24*60.0,DAY,24*60.0),
    //月按周计算(按照apm的标准，周按每天1个点)
    ONE_MONTH(6, 30*24*60.0,WEEK,24*60.0),
//    ONE_MONTH(6, 30*24*60.0,WEEK,7*24*60.0),
    //年按月计算
    ONE_YEAR(7, 365*24*60.0,MONTH,24*60.0),
//    ONE_YEAR(7, 365*24*60.0,MONTH,30*24*60.0),
    CUSTOM_TIME(8, null,null,null);


    private Integer code;
    private Double value;
    //时间单位
    private String unit;
    //分组统计方式
    private Double groupingMethod;

    /**
     * 计算两个日期【日期类型】之间的时间差值（按分钟计算），然后计算分组统计的方式
     */

    /**
     * 通过分组统计方式
     *
     * @param code : 代码
     * @return String : 值
     */
    public static Double getGroupingMethod(Integer code) {
        for (TimeEnums c : TimeEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.groupingMethod;
            }
        }
        return null;
    }

    /**
     * 通过代码获取单位
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getUnit(Integer code) {
        for (TimeEnums c : TimeEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.unit;
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
    public static Double getValue(Integer code) {
        for (TimeEnums c : TimeEnums.values()) {
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
        for (TimeEnums c : TimeEnums.values()) {
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
    private TimeEnums(Integer code, Double value,String unit,Double groupingMethod) {
        this.code = code;
        this.value = value;
        this.unit = unit;
        this.groupingMethod = groupingMethod;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getGroupingMethod() {
        return groupingMethod;
    }

    public void setGroupingMethod(Double groupingMethod) {
        this.groupingMethod = groupingMethod;
    }

}
