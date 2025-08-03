package com.ly.cloud.backup.common.elasticsql;

/**
 *  监控面板- mongo 面板sql
 * */
public class PanelMongoSql {
    // ip,状态 status现行的连接为零是否可认为是异常状态 ，还是说到那里先查一下数据库，把监控的mongodb与现在查出来的对比一下看那个没有设置为异常？
    public static String StatusIPMongoSql="{\"query\":\"select \\\"@timestamp\\\",service.address ip" + //,mongodb.status.connections.current status
            " from \\\"metricbeat-*\\\" " +
            " where event.module='mongodb' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by \\\"@timestamp\\\", ip"+ //, status
            " order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\" }";
    //DOCKER ECS 节点 表格 --这个可能是名字错了
    public static String HostsECSNodeMongoSql ="'{'\"query\":\"select \\\"@timestamp\\\",service.address ip,mongodb.status.connections.current conn, host.architecture arch,mongodb.status.memory.bits total, mongodb.status.memory.resident.mb resident,mongodb.status.memory.virtual.mb virtual " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by \\\"@timestamp\\\",ip,conn,arch,total,resident,virtual "+
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //引擎与版本
    public static String EngineVersionMongoSql="'{'\"query\":\"select mongodb.status.storage_engine.name engine,service.version version " + //\"@timestamp\",
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by engine, version"+ //\"@timestamp\" ,
            " \",\"time_zone\":\"Asia/Shanghai\" '}'"; //\"@timestamp\" desc
    //运算计数器
    public static String OpsCounterMongoSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mongodb.status.ops.counters.command command,mongodb.status.ops.counters.delete delete,mongodb.status.ops.counters.getmore getmore, mongodb.status.ops.counters.insert insert, mongodb.status.ops.counters.query query,mongodb.status.ops.replicated.update update " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp ,command,delete,getmore,insert,query,update "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //并发事务写入 ---带告警值多折线
    public static String ConcurrentTransactionReadMongoSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mongodb.status.wired_tiger.concurrent_transactions.read.available available,mongodb.status.wired_tiger.concurrent_transactions.read.out out " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,available,out "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //并发事务读取 ---带告警值多折线
    public static String ConcurrentTransactionWriteMongoSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mongodb.status.wired_tiger.concurrent_transactions.write.available available,mongodb.status.wired_tiger.concurrent_transactions.write.out out "+
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, available,out"+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //WiredTiger缓存 ---带告警值多折线
    public static String WiredTigerCacheMongoSql="'{'\"query\":\"select histogram(\"@timestamp\",interval '10' second) timestamp, mongodb.status.wired_tiger.cache.maximum.bytes byte "+
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            "  group by timestamp,byte  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String WiredTigerCacheMongoQueryDSL="\n" +
            "{\n" +
            "  \"size\": 0,\n" +
            "    \"query\": {\n" +
            "        \"bool\": {\n" +
            "            \"must\": [\n" +
            "                {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "                ,{\"match\": {\"event.module\": \"mongodb\"}}\n" +
            "                \n" +
            "              ]\n" +
            "        }\n" +
            "    },\n" +
            "    \"aggs\": {\n" +
            "        \"linevalues\": {\n" +
            "            \"date_histogram\": {\n" +
            "              \"field\": \"@timestamp\",\n" +
            "              \"calendar_interval\": \"minute\"\n" +
            "            },\n" +
            "            \"aggs\": {\n" +
            "                \"dirty\": {\n" +
            "                    \"avg\": {\n" +
            "                        \"field\": \"mongodb.status.wired_tiger.cache.dirty.bytes\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"used\": {\n" +
            "                    \"avg\": {\n" +
            "                        \"field\": \"mongodb.status.wired_tiger.cache.used.bytes\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"maximum\": {\n" +
            "                    \"avg\": {\n" +
            "                        \"field\": \"mongodb.status.wired_tiger.cache.maximum.bytes\"\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}" +
            "";
    //断言
    public static String AssertsMongoSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mongodb.status.asserts.msg msg,mongodb.status.asserts.regular regular, mongodb.status.asserts.rollovers rollovers, mongodb.status.asserts.user user,mongodb.status.asserts.warning warning "+
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,msg, regular, rollovers,user,warning   \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //内存统计信息
    public static String MemoryStaticMongoSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mongodb.status.memory.mapped.mb mapped,mongodb.status.memory.mapped_with_journal.mb journal, mongodb.status.memory.resident.mb resident, mongodb.status.memory.virtual.mb virtual"+
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mongodb''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,mapped,journal,resident,virtual  \",\"time_zone\":\"Asia/Shanghai\" '}'";
}

