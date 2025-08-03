package com.ly.cloud.backup.common.elasticsql;

/**
 * mysql 指标相关elasticsearch sql
 */
public class PanelMysqlSql {
    //mysql 数据库url列表
    public static final String MysqlIPSql="{\"query\": \"select service.address ip" + //\"@timestamp\",
            " from \\\"metricbeat-*\\\" " +
            " where event.module='mysql' " +
//            " and event.dataset='mysql' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  ip"+
            " \",\"time_zone\":\"Asia/Shanghai\" }"; //order by \"@timestamp\" desc

    //错误率 ---最大错误数？
    public static final String ErrorPctSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,mysql.status.connection.errors.max maxError" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and mysql.status.connection.errors.max is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            "  group by timestamp ,maxError   \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //选择语句比率
    public static final String SelectQueryPctSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,\\\"mysql.status.command.select\\\" selectQuery, \\\"mysql.status.command.update\\\" updateQuery, \\\"mysql.status.command.delete\\\" deleteQuery, \\\"mysql.status.command.insert\\\" insertQuery" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"mysql.status.command.select\\\" is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            "  group by timestamp,selectQuery, updateQuery, deleteQuery,insertQuery  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //添加，更新，删除速率
    public static final String InsertUpdateDeleteQueryPctSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,\\\"mysql.status.command.select\\\" selectQuery, \\\"mysql.status.command.update\\\" updateQuery, \\\"mysql.status.command.delete\\\" deleteQuery, \\\"mysql.status.command.insert\\\" insertQuery" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"mysql.status.command.select\\\" is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            "  group by timestamp,selectQuery, updateQuery, deleteQuery,insertQuery  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //链接线程数（Thread connected）连接速率，连接数，已用连接数
    public static final String ConnectionSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mysql.status.connections connections,mysql.status.threads.connected connected, mysql.status.max_used_connections usedConn " +//缺少连接速率
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " and mysql.status.connections is not null "+
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            "  group by timestamp,connections,connected,usedConn order by timestamp desc \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //活动线程数（Thread Activity）运行平均线程数，运行峰值线程数，连接线程数峰值
    public static final String ActivityThreadsSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mysql.status.threads.running running,  mysql.status.threads.connected connected" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " and mysql.status.threads.connected is not null  "+
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            "  group by timestamp,running,connected order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //终止连接速率
    public static final String AbortedClientsSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, max(\\\"mysql.status.aborted.clients\\\") clients, max(\\\"mysql.status.aborted.connects\\\") connects" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            "  group by timestamp    \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //运行，峰值，连接线程数
    public static final String ThreadRunningConnectedSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,avg(\\\"mysql.status.threads.running\\\") avgRunning, max(\\\"mysql.status.threads.running\\\") maxRunning, max(\\\"mysql.status.threads.connected\\\") connected" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            "  group by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //缓冲池
    public static final String  BufferedPoolSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,avg(\\\"mysql.status.innodb.buffer_pool.pages.data\\\") data, avg(\\\"mysql.status.innodb.buffer_pool.pages.free\\\") free, avg(\\\"mysql.status.innodb.buffer_pool.pages.total\\\") total" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //缓冲池利用率
    public static final String BufferedPoolPctSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,\\\"mysql.status.innodb.buffer_pool.pages.free\\\" free,\\\"mysql.status.innodb.buffer_pool.pages.total\\\" total" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"mysql.status.innodb.buffer_pool.pages.free\\\" is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp,free,total  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //缓冲池效率
    public static final String BufferPoolEfficiency="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,max(\\\"mysql.status.innodb.buffer_pool.pool.reads\\\"), max(\\\"mysql.status.innodb.buffer_pool.read.requests\\\") efficiency" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //网络流量 --TODO 未找到相关指标，是否用system.network.in.bytes 和 system.network.out.bytes代替
    public static final String NetworkIOSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,mysql.status.bytes.received receivedBytes,mysql.status.bytes.sent transmittedBytes" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and mysql.status.bytes.received is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp,receivedBytes,transmittedBytes  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //打开表，文件，流
    public static final String OpenedTableFileStreamSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,avg(\\\"mysql.status.open.tables\\\") tables, avg(\\\"mysql.status.open.files\\\") files,avg(\\\"mysql.status.open.streams\\\") streams" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //打开表缓存 未找到指标
    public static final String OpenTableCachesSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, \\\"mysql.status.cache.table.open_cache.hits\\\" hits, \\\"mysql.status.cache.table.open_cache.misses\\\" misses,\\\"mysql.status.cache.table.open_cache.overflows\\\" overflows" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and (\\\"mysql.status.cache.table.open_cache.hits\\\" is not null or \\\"mysql.status.cache.table.open_cache.misses\\\" is not null or \\\"mysql.status.cache.table.open_cache.overflows\\\" is not null)"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp,hits,misses,overflows  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //连接错误 未找到指标  mysql.status.connection.errors.accept accept, mysql.status.connection.errors.internal internal,mysql.status.connection.errors.max max,mysql.status.connection.errors.peer_address peer_address
    public static final String ConnectionErrorSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp, mysql.status.connection.errors.accept accept, mysql.status.connection.errors.internal internal,mysql.status.connection.errors.max max,mysql.status.connection.errors.peer_address peer_address" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and (mysql.status.connection.errors.accept is not null or  mysql.status.connection.errors.internal is not null or mysql.status.connection.errors.max is not null or mysql.status.connection.errors.peer_address is not null)"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            "  group by timestamp,accept,internal,max, peer_address  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //命令操作 --max(\"mysql.status.command.select\") select是保留字要写成selectSql
    public static final String CommandsOperationsSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,max(\\\"mysql.status.command.select\\\") selectSql, max(\\\"mysql.status.command.insert\\\") insert, max(\\\"mysql.status.command.update\\\") update, max(\\\"mysql.status.command.delete\\\") delete" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //SSL 缓存
    public static final String SSLCacheSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,max(\\\"mysql.status.cache.ssl.hits\\\") hits, max(\\\"mysql.status.cache.ssl.misses\\\") misses, max(\\\"mysql.status.cache.ssl.size\\\") size" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and service.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '10' SECOND "+
            " group by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //连接数 ---下面的没有用了2023.1.6
    public static final String MaxConnectionSql="'{'\"query\": \"select \\\"@timestamp\\\",mysql.query.connects.max_connections connections" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and event.dataset='''mysql.query''' " +
            " and service.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " group by timestamp limit 1\",\"time_zone\":\"Asia/Shanghai\" '}'";

    //表空间
    public static final String TablespaceSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.username,  service.address,  sql.metrics.string.tablespace_name, sql.metrics.string.autoextensible, sql.metrics.numeric.usage_amount, sql.metrics.numeric.amounts,sql.metrics.numeric.usage_rate" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and event.dataset='''mysql.query''' " +
            " and sql.metrics.string.oracle_metric_type='''temp_tablespace''' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\ \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //SQL连接数
    public static final String SqlConnectionsSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,service.address, mysql.status.threads.connected" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and event.dataset='''mysql.status''' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\\" service.address, mysql.status.threads.connected\",\"time_zone\":\"Asia/Shanghai\" '}'";
    //SQL查询量统计
    public static final String SqlQueriesStaticsSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,max(\\\"mysql.status.command.insert\\\") insert_count, max(\\\"mysql.status.command.update\\\") update_count, max(\\\"mysql.status.command.delete\\\") delete_count, max(\\\"mysql.status.command.select\\\") select_count" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and event.dataset='''mysql.status''' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\\" service.address\",\"time_zone\":\"Asia/Shanghai\" '}'";

    //内存结构-SGA、PGA内存使用率
    public static final String SgaPgaMemoryPctSql="'{'\"query\": \"select \\\"@timestamp\\\" timestamp,sql.metrics.string.oracle_metric_type, service.address, sql.metrics.numeric.total, sql.metrics.numeric.used,sql.metrics.numeric.pctused " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and sql.metrics.string.oracle_metric_type IN (''sga'',''pga'')" +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\\" desc limit 2 \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //内存结构-SGA组件内存结构使用前十
    public static final String SgaMemoryTo10Sql="'{'\"query\": \"select \\\"@timestamp\\\" timestamp,sql.metrics.string.oracle_metric_type, service.address, sql.metrics.numeric.total, sql.metrics.numeric.used,sql.metrics.numeric.pctused " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''mysql''' " +
            " and sql.metrics.string.oracle_metric_type = '''sga''' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\\" desc limit 10 \",\"time_zone\":\"Asia/Shanghai\" '}'";
}



//" and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
//        " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +