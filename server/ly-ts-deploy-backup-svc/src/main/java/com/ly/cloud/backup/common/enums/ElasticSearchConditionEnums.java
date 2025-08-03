package com.ly.cloud.backup.common.enums;

import java.util.Calendar;
import java.util.Objects;

/**
 * es搜索条件枚举类
 *
 * @author: jiangzhongxin
 */
public enum ElasticSearchConditionEnums {

    /**
     * 按秒查询
     */
    DATE_ENUMS_SECOND(1, "SECOND", 19),
    /**
     * 按分钟查询
     */
    DATE_ENUMS_MINUTE(2, "MINUTE", 19),
    /**
     * 按小时查询
     */
    DATE_ENUMS_HOUR(3, "HOUR", 16, Calendar.MINUTE, 60, 1),
    /**
     * 按天查询
     */
    DATE_ENUMS_DAY(4, "DAY", 13, Calendar.HOUR_OF_DAY, 24, 60),
    /**
     * 按周查询
     */
    DATE_ENUMS_WEEK(5, "WEEK", 13, Calendar.DATE, 7, 24 * 60),
    /**
     * 按月查询
     */
    DATE_ENUMS_MONTH(6, "MONTH", 10, Calendar.WEEK_OF_MONTH, 4, 7 * 24 * 60),
    /**
     * 按年查询
     */
    DATE_ENUMS_YEAR(7, "YEAR", 7, Calendar.MONTH, 12, 30 * 24 * 60);

    private Integer code;
    private String value;
    private Integer endIndex;
    private Integer field;
    private Integer len;
    private Integer dividend;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
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
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getCode(String value) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
            if (c.getValue().equals(value)) {
                return c.getCode();
            }
        }
        return null;
    }

    /**
     * 通过值获取endIndex
     *
     * @param value : 值
     * @return Integer : endIndex
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getEndIndex(String value) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
            if (Objects.equals(value, c.getValue())) {
                return c.getEndIndex();
            }
        }
        return null;
    }

    /**
     * 通过值获取endIndex
     *
     * @param value : 值
     * @return Integer : field
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getField(String value) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
            if (Objects.equals(value, c.getValue())) {
                return c.getField();
            }
        }
        return null;
    }

    /**
     * 通过值获取endIndex
     *
     * @param value : 值
     * @return Integer : len
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getLen(String value) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
            if (Objects.equals(value, c.getValue())) {
                return c.getLen();
            }
        }
        return null;
    }

    /**
     * 通过值获取endIndex
     *
     * @param value : 值
     * @return Integer : dividend
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getDividend(String value) {
        for (ElasticSearchConditionEnums c : ElasticSearchConditionEnums.values()) {
            if (Objects.equals(value, c.getValue())) {
                return c.getDividend();
            }
        }
        return null;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     * @param endIndex : 结束索引
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private ElasticSearchConditionEnums(Integer code, String value, Integer endIndex) {
        this.code = code;
        this.value = value;
        this.endIndex = endIndex;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     * @param endIndex : 结束索引
     * @param field : 日历字段
     * @param len : 时间跨度参数
     * @author jiangzhongxin
     * @date 2022/3/28 13:42
     */
    private ElasticSearchConditionEnums(Integer code, String value, Integer endIndex, Integer field, Integer len) {
        this.code = code;
        this.value = value;
        this.endIndex = endIndex;
        this.field = field;
        this.len = len;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     * @param endIndex : 结束索引
     * @param field : 日历字段
     * @param len : 时间跨度参数
     * @param dividend : 除以的参数
     * @author jiangzhongxin
     * @date 2022/3/28 13:42
     */
    private ElasticSearchConditionEnums(Integer code, String value, Integer endIndex, Integer field, Integer len, Integer dividend) {
        this.code = code;
        this.value = value;
        this.endIndex = endIndex;
        this.field = field;
        this.len = len;
        this.dividend = dividend;
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

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getField() {
        return field;
    }

    public void setField(Integer field) {
        this.field = field;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public Integer getDividend() {
        return dividend;
    }

    public void setDividend(Integer dividend) {
        this.dividend = dividend;
    }

}
