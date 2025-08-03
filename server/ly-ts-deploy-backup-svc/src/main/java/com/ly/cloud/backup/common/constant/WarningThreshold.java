package com.ly.cloud.backup.common.constant;
/**
 * @Author zlx 2023-02-07 10:48
 * 监控面板图表阈值常量 --对应表ly_mw_warning_target里的预设置值
 * */
public enum WarningThreshold {
    HOST_CPU_PCT("cpuPct","主机CPU使用率",1002L),
    HOST_MEMORY_PCT("memoryPct","主机内存使用率",1003L),
    HOST_DISK_PCT("diskUsage","系统硬盘使用率",1004L),
    HOST_DISK_SHARD_PCT("diskShardPct","硬盘/分区使用率",1005L),

    MYSQL_CPU_PCT("mysqlCpuPct","mysql的CPU使用率",2001L),
    MYSQL_MEMORY_PCT("mysqlMemoryPct","mysql内存使用率",2002L),
    ORACLE_CPU_PCT("oracleCpuPct","oracle的CPU使用率",2003L),
    ORACLE_MEMORY_PCT("oracleMemoryPct","oracle内存使用率",2004L),

    SVC_CPU_PCT("jvmCpuPct","服务CPU使用率",3001L),
    SVC_MEMORY_PCT("svcMemoryPct","服务内存使用率",3002L),
    JVM_STACK_PCT("jvmStackPct","Jvm堆使用率",3003L),

    SVC_THROUGHPUT("svcThroughput","服务吞吐量告警",3005L),
    INSTANCE_THREAD_TOO_MANY("instanceThreadTooMany","运行实例线程数告警",3006L),
    SVC_HEALTH_MONITOR("svcHealthMonitor","应用健康监测",4001L);

    private String index;
    private String zh;
    private Long id;

    public String getIndex(){
        return index;
    }
    public String getZh(){
        return zh;
    }

    public Long getId(){
        return  id;
    }

    WarningThreshold(String index,String zh,Long id){
        this.index=index; this.zh=zh;this.id=id;
    }

}
