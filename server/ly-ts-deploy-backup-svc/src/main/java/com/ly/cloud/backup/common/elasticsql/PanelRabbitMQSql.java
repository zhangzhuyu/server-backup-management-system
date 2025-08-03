package com.ly.cloud.backup.common.elasticsql;

/**
 *  监控面板- rabbitmq 面板sql
 * */
public class PanelRabbitMQSql {

    //ip地址和 rabbitmq 状态
    public static String StatusIPRabbitmqSql="{\"query\":\"select service.address ip" + //\"@timestamp\", , status
            " from \\\"metricbeat-*\\\" " +
            " where event.module='rabbitmq' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  ip"+ //\"@timestamp\", , status
            " \",\"time_zone\":\"Asia/Shanghai\" }"; //order by \"@timestamp\" desc

    //内存使用情况 单折线图
    public static String MemoryUsageRabbitmqSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,rabbitmq.node.name name,rabbitmq.node.mem.used.bytes bytes " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''rabbitmq''' " +
            " and service.address like ''{0}'' "+
            " and rabbitmq.node.mem.used.bytes is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,name,bytes"+
            "  \",\"time_zone\":\"Asia/Shanghai\" '}'"; //order by \"@timestamp\" desc

    //节点数
    public static String NodeNumRabbitmqSql="'{'\"query\":\"select \\\"@timestamp\\\",rabbitmq.node.name name" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''rabbitmq''' " +
//            " and service.address like ''{0}'' "+
            " and rabbitmq.node.name is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by name, \\\"@timestamp\\\" "+
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //Erlang进程使用 --带告警线折线图
    public static String ErlangProcessRabbitmqSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,rabbitmq.node.name name,rabbitmq.node.proc.used pct" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''rabbitmq''' " +
            " and service.address like ''{0}'' "+
            " and rabbitmq.node.proc.used is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,name,pct"+
            " \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //队列索引操作 --带告警线折线图
    public static String QueueIndexOpsRabbitmqSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,rabbitmq.node.name name,rabbitmq.node.queue.index.read.count  read, rabbitmq.node.queue.index.journal_write.count  journalWrite,   rabbitmq.node.queue.index.write.count  write" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''rabbitmq''' " +
            " and service.address like ''{0}'' "+
            " and (rabbitmq.node.queue.index.read.count is not null or rabbitmq.node.queue.index.journal_write.count is not null or rabbitmq.node.queue.index.write.count is not null )"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,name,read,journalWrite,write"+
            "  \",\"time_zone\":\"Asia/Shanghai\" '}'";

}
