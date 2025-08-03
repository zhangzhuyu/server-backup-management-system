package com.ly.cloud.backup.common.elasticsql;

/**
 *  监控面板- redis 面板sql    public static String
 * */
public class PanelRedisSql {
    //IP 状态
    public static String StatusIPRedisSql="{\"query\":\"select service.address ip, redis.info.server.uptime status" + //\"@timestamp\",
            " from \\\"metricbeat-*\\\" " +
            " where event.module='redis' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  ip, status"+ //\\\"@timestamp\\\",
            " \",\"time_zone\":\"Asia/Shanghai\" }"; // order by \\\"@timestamp\\\" desc
    //概览
    public static String SummaryRedisSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,service.version version, redis.info.memory.used.value memory,redis.info.clients.connected clients,redis.info.stats.commands_processed commandsProcessed,redis.info.server.uptime upTime" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''redis''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,memory,version,clients,commandsProcessed,upTime  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //内存占用拆线图
    public static String MemoryUsedRedisSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,redis.info.memory.used.value total,redis.info.memory.used.dataset dataset ,redis.info.memory.used.lua lua ,redis.info.memory.used.rss rss,redis.info.memory.used.peak peak" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''redis''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,total, dataset,lua,rss,peak \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //服务器信息 多tab列表---第一步用clients/cluster/commands/cpu/keyspace/memory/persistence/replication/server/stats代入{0},再查
    public static String DataSetRedisSql="'{'\"query\":\"select \\\"@timestamp\\\",redis.info.{0}.*" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''redis''' " +
            " and service.address like ''{1}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //慢查询日志 --列表 未找到相关指标redis.info.slowlog.count count还是说是redis.info.replication.backlog.active --改从filebeat里查找在kibana里有相关指标
    //https://www.elastic.co/guide/en/beats/filebeat/current/exported-fields-redis.html
    public static String SlowLogRedisSql="'{'\"query\":\"select \\\"@timestamp\\\" timestamp, redis.slowlog.cmd command,redis.slowlog.duration.us duration" +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''redis''' " +
//            " and event.dataset='''redis.slowlog''' "+
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //下面这个是不对的，正确的如SlowLogRedisCorrectDSL，错误的原因是match是等于，
    // 代入host.ip_str后不能是等于的，必须用wildcard来做like查询，并且必须给192.168.34.136前后加上*号（不加*号也是等于查询）
    public static String SlowLogRedisQueryDSL="\n" +
            "{\n" +
            "  \"from\": 0, \n" +
            "  \"size\": 10,\n" +
            "  \"sort\" : [\n" +
            "    { \"redis.slowlog.duration.us\" : \"desc\" }\n" +
            "  ],\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"redis.slowlog\" }},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";
    public static String SlowLogRedisCorrectDSL="{\n" +
            "  \"from\": 0, \n" +
            "  \"size\": 10,\n" +
            "   \"sort\" : [\n" +
            "    { \"redis.slowlog.duration.us\" : \"desc\" }\n" +
            "  ],\n" +
            "  \"fields\": [\n" +
            "    \"redis.slowlog.duration.us\",\"redis.slowlog.duration.cmd\",\"redis.slowlog.duration.key\"\n" +
            "  ], \n" +
            "  \"query\": {\n" +
            "    \"bool\": {\"must\": [\n" +
            "      { \"wildcard\": {\n" +
            "      \"host.ip_str\": {\n" +
            "        \"value\": \"*192.168.34.136*\"\n" +
            "      }\n" +
            "    }},\n" +
            "    {\n" +
            "      \"match\": {\n" +
            "        \"event.module\": \"redis\"\n" +
            "      }\n" +
            "    }\n" +
            "    ]}\n" +
            "  }\n" +
            "}";
    //推送订阅通知 --列表 不对，官方采集器只有redis.info.stats.pubsub.channels 渠道数量，没有订阅渠道名等
    public static String PubSubChannelRedisSql="'{'\"query\":\"select \\\"@timestamp\\\" timestamp,redis.info.stats.pubsub.channels channels,redis.info.stats.pubsub.patterns patterns" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''redis''' " +
            " and service.address like ''{0}'' "+
            " and redis.info.stats.pubsub.channels is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //客户端 --列表 未找到相关指标 --找不到这些指标idle, mark, currentdatabase用redis 仪表板的原图代替
    public static String ClientsRedisSql="'{'\"query\":\"select \\\"@timestamp\\\" timestamp, service.address ip, redis.info.server.uptime uptime, process.pid pid,redis.info.memory.used.value memory,redis.info.cpu.used.sys sysCPU, redis.info.cpu.used.user userCPU " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''redis''' " +
            " and service.address like ''{0}'' "+
            " and uptime is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
}
