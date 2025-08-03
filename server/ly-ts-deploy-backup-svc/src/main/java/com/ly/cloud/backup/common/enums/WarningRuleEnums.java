package com.ly.cloud.backup.common.enums;

/**
 * 内置的告警规则 公共枚举类
 *
 * @author: SYC
 * @since 2023-02-22
 */
public enum WarningRuleEnums {

    /**
     * 内置的告警规则
     */
    SYSTEM_STATUS(1001, "主机状态监控"),
    SYSTEM_CPU_RATE(1002, "主机CPU使用率监控"),
    SYSTEM_RAM_RATE(1003, "主机内存使用率监控"),
    SYSTEM_DISK_RATE(1004, "系统磁盘使用率"),
    SYSTEM_DISK_SUBAREA_RATE(1005, "磁盘分区使用率监控"),
    SYSTEM_DISK_SWAP_RATE(1006, "磁盘swap分区使用率监控"),
    MYSQL_CPU_RATE(2001, "mysql的CPU使用量监控"),
    MYSQL_RAM_RATE(2002, "mysql内存使用率"),
    ORACLE_CPU_RATE(2003, "oracle的CPU使用率监控"),
    ORACLE_RAM_RATE(2004, "oracle的内存使用率"),
    DATABASE_STATUS(2005, "数据库运行状态"),
    SERVICE_CPU_RATE(3001, "服务CPU使用率监控"),
    SERVICE_RAM_RATE(3002, "服务内存使用率"),
    SERVICE_JVM_RATE(3003, "服务jvm堆使用率"),
    SERVICE_STATUS(3004, "服务状态监控"),
    SERVICE_TPM(3005, "服务吞吐量"),
    SERVICE_INSTANCE_THREAD_COUNT(3006, "服务运行实例线程数告警"),
    SERVICE_INTERFACE_ERROR(3007, "服务接口错误次数告警"),
    SERVICE_LATENCY_TPM(3008, "服务延迟与吞吐量"),
    SERVICE_LATENCY(3009, "服务延迟"),
    APP_STATUS(4001, "应用健康状态监控"),
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
        for (WarningRuleEnums c : WarningRuleEnums.values()) {
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
        for (WarningRuleEnums c : WarningRuleEnums.values()) {
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
    private WarningRuleEnums(Integer code, String value) {
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
