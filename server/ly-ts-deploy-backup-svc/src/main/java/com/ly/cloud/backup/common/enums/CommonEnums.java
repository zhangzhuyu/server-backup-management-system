package com.ly.cloud.backup.common.enums;

/**
 * 公共枚举类
 *
 * @author: jiangzhongxin
 */
public enum CommonEnums {

    /**
     * 是否启用 0 禁用 1 启用
     */
    STATUS_DISABLE(0, "禁用"),
    STATUS_ENABLE(1, "启用"),

    /**
     * 请求状态 0 异常 1 正常
     */
    REQUEST_STATUS_ALL(-1, "全部"),
    REQUEST_STATUS_ABNORMAL(0, "异常"),
    REQUEST_STATUS_NORMAL(1, "正常"),


    /**
     * 系统错误 参数错误
     */
    SYSTEM_ERROR(10001, "系统繁忙，请稍后重试"),
    PARAM_ERROR(10002, "参数有误"),

    /**
     * 测试状态 0 失败 1 成功
     */
    TEST_STATUS_FAIL(0, "失败"),
    TEST_STATUS_SUCCESS(1, "成功"),

    /**
     * 告警记录是否忽略 0 不忽略 1 忽略
     */
    ALARM_RECORD_NOT_IGNORE(0, "不忽略"),
    ALARM_RECORD_IGNORE(1, "忽略"),

    /**
     * 处理状态 -1 待处理 0 正在处理 1 已处理
     */
    HANDLE_STATUS_PENDING(-1, "待处理"),
    ALARM_TYPE_PROCESSING(0, "正在处理"),
    HANDLE_STATUS_PROCESSED(1, "已处理"),

    /**
     * 指标对象 1 主机
     */
    TARGET_OBJECT_SERVER(1, "主机"),
    /**
     * 指标对象 2 数据库
     */
    TARGET_OBJECT_DATABASE(2, "数据库"),
    /**
     * 指标对象 3 服务
     */
    TARGET_OBJECT_SERVICE(3, "服务"),

    /**
     * 指标对象 4 应用
     */
    TARGET_OBJECT_APPLICATION(4, "应用"),

    /**
     * 指标对象 5 中间件
     */
    TARGET_OBJECT_MIDDLEWARE(5, "中间件"),

    /**
     * 指标返回结果类型 1 百分制
     */
    TARGET_TYPE_PCT(1,"百分制"),

    /**
     * 指标返回结果类型 2 数值
     */
    TARGET_TYPE_NUM(2,"数值"),
    /**
     * 指标返回结果类型 3 字符
     */
    TARGET_TYPE_STR(3,"字符"),
    /**
     * 指标对象 4 网站存活性
     */
    TARGET_OBJECT_WEBSITE_SURVIVABILITY(4, "应用"),

    ALARM_LEVEL_COMMONLY(1, "一般"),
    ALARM_LEVEL_WARNING(2, "警告"),
    ALARM_LEVEL_SERIOUS(3, "严重"),
    ALARM_LEVEL_URGENT(4, "紧急"),

    /**
     * 指标类型 1 服务器宕机
     */
    TARGET_TYPE_SERVER_DOWNTIME(1, "服务器宕机"),
    /**
     * 指标类型 2 服务状态
     */
    TARGET_TYPE_SERVICE_STATUS(2, "服务状态"),
    /**
     * 指标类型 3 CPU使用率
     */
    TARGET_TYPE_CPU_USAGE(3, "CPU使用率"),
    /**
     * 指标类型 4 内存使用率
     */
    TARGET_TYPE_MEMORY_USAGE(4, "内存使用率"),
    /**
     * 指标类型 5 系统盘使用率
     */
    TARGET_TYPE_SYSTEM_DISK_USAGE(5, "系统盘使用率"),

    /**
     * 阈值指标范围 1 >=
     */
    THRESHOLD_RANGE_GTEQ(1, ">="),
    /**
     * 阈值指标范围 2 <=
     */
    THRESHOLD_RANGE_LTEQ(2, "<="),
    /**
     * 阈值指标范围 3 ==
     */
    THRESHOLD_RANGE_EQEQ(3, "=="),

    /**
     * 数据源类型 1 Oracle
     */
    SOURCE_TYPE_ORACLE(1, "Oracle"),
    /**
     * 数据源类型 2 MySQL
     */
    SOURCE_TYPE_MYSQL(2, "MySQL"),
    /**
     * 数据源类型 3 SQLServer
     */
    SOURCE_TYPE_SQLSERVER(3, "SQLServer"),
    /**
     * 数据源类型 3 PostgreSQL
     */
    SOURCE_TYPE_POSTGRE_SQL(4, "PostgreSQL"),
    /**
     * 数据源类型 5 KingBase
     */
    SOURCE_TYPE_KINGBASE(5, "KingBase"),

    /**
     * 是否加入运维大屏 0 否
     */
    WHETHER_MONITORING_NO(0, "否"),
    /**
     * 是否加入运维大屏 1 是
     */
    WHETHER_MONITORING_YES(1, "是");

    private Integer code;
    private String value;

    /**
     * 通过代码获取值
     *    REQUEST_STATUS_ABNORMAL(0, "异常"),
     *     REQUEST_STATUS_NORMAL(1, "正常"),
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getYesOrNoValue(Integer code) {
        if (WHETHER_MONITORING_YES.getCode().equals(code)){
            return WHETHER_MONITORING_YES.getValue();
        }else  if (WHETHER_MONITORING_NO.getCode().equals(code)){
            return WHETHER_MONITORING_NO.getValue();
        }else {
            return String.valueOf(code);
        }
    }


    /**
     * 通过代码获取值
     *    REQUEST_STATUS_ABNORMAL(0, "异常"),
     *     REQUEST_STATUS_NORMAL(1, "正常"),
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getHealthStatusValue(Integer code) {
        if (REQUEST_STATUS_ABNORMAL.getCode().equals(code)){
            return REQUEST_STATUS_ABNORMAL.getValue();
        }else  if (REQUEST_STATUS_NORMAL.getCode().equals(code)){
            return REQUEST_STATUS_NORMAL.getValue();
        }else {
            return String.valueOf(code);
        }
    }

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (CommonEnums c : CommonEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.value;
            }
        }
        return null;
    }

    /**
     * 通过代码获取值
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getAlarmLevelValue(Integer code) {
        if (ALARM_LEVEL_COMMONLY.getCode().equals(code)){
            return ALARM_LEVEL_COMMONLY.getValue();
        }else  if (ALARM_LEVEL_WARNING.getCode().equals(code)){
            return ALARM_LEVEL_WARNING.getValue();
        }else  if (ALARM_LEVEL_SERIOUS.getCode().equals(code)){
            return ALARM_LEVEL_SERIOUS.getValue();
        }else  if (ALARM_LEVEL_URGENT.getCode().equals(code)){
            return ALARM_LEVEL_URGENT.getValue();
        } else {
            return String.valueOf(code);
        }
    }

    /**
     * 通过代码获取值
     * @param code : 代码
     * @return String : 值
     * @date 2022/3/23 13:41
     */
    public static String getTargetObjectValue(Integer code) {
        if (TARGET_OBJECT_SERVER.getCode().equals(code)){
            return TARGET_OBJECT_SERVER.getValue();
        }else  if (TARGET_OBJECT_DATABASE.getCode().equals(code)){
            return TARGET_OBJECT_DATABASE.getValue();
        }else  if (TARGET_OBJECT_SERVICE.getCode().equals(code)){
            return TARGET_OBJECT_SERVICE.getValue();
        }else  if (TARGET_OBJECT_APPLICATION.getCode().equals(code)){
            return TARGET_OBJECT_APPLICATION.getValue();
        }else  if (TARGET_OBJECT_MIDDLEWARE.getCode().equals(code)){
            return TARGET_OBJECT_MIDDLEWARE.getValue();
        } else {
            return String.valueOf(code);
        }
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
        for (CommonEnums c : CommonEnums.values()) {
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
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private CommonEnums(Integer code, String value) {
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
