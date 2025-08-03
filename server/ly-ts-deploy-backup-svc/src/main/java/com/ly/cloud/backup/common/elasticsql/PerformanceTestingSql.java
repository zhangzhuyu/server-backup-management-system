package com.ly.cloud.backup.common.elasticsql;

public class PerformanceTestingSql {

    public static String HardwareSql="'{'\"query\":\"select * from \\\"hardware-*\\\" where serverId = ''{0}'' order by finishTime desc limit 1\",\"time_zone\":\"Asia/Shanghai\"'}'";
}
