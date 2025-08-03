package com.ly.cloud.backup.common.elasticsql;

/**
 * oracle监控面板图表Sql   指标网址：https://www.elastic.co/guide/en/beats/metricbeat/7.6/metricbeat-metricset-oracle-performance.html
 * */
public class PanelOracleSql {
    public static final String OracleIPs="{\"query\": \"select  service.address ip" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='sql' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by ip \"}";
    //ip,driver等字段不能与count() session一起用，不能一部分聚合，一部分不聚合2023.01.15
    public static final String SummarySql0 ="'{'\"query\": \"select \\\"@timestamp\\\",service.address ip,sql.driver driver,sql.metrics.string.instance_name instance,sql.metrics.string.oracle_version version,sql.metrics.string.database_status dbStatus,sql.metrics.string.status instanceStatus " + //sql.metrics.numeric.connect_count connections,sql.metrics.numeric.connect_max maxConnections,,sql.metrics.numeric.sid sessions
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " and  sql.metrics.string.oracle_metric_type='''version''' "+
            " order by \\\"@timestamp\\\" desc limit 1 \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    public static final String SummarySql1 ="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.numeric.connect_count connections,sql.metrics.numeric.connect_max maxConnections " + //
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and (sql.metrics.numeric.connect_count is not null or sql.metrics.numeric.connect_max is not null  )"+
            " order by \\\"@timestamp\\\" desc \" ,\"time_zone\":\"Asia/Shanghai\"  '}'";

    public static final String SummarySql2 ="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.numeric.sid sessions " + //
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and sql.metrics.numeric.sid is not null  "+
            " order by \\\"@timestamp\\\" desc \" ,\"time_zone\":\"Asia/Shanghai\"  '}'";

    //用户表空间sql.metrics.numeric.amounts,    sql.metrics.numeric.usage_rate
    public static final String UserTableSpaceSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.tablespace_name tablespace, sql.metrics.numeric.usage_amount userAmount" + //(sql.metrics.numeric.usage_amount)/1024/1024
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " and sql.metrics.string.oracle_metric_type='''tablespace''' "+
            " group by  \\\"@timestamp\\\", tablespace,userAmount "+
            " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //临时表空间
    public static final String TempTableSpaceSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.tablespace_name tablespace, sql.metrics.numeric.usage_amount userAmount" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " and sql.metrics.string.oracle_metric_type='''temp_tablespace''' "+
            " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //sql IO开销 （TOP 10）表空间IO开销---此指标未找到 2023.02.24用sql io开销代替
    //sql_query: "select * from (select v.sql_id,v.child_number,v.sql_text,v.elapsed_time,v.cpu_time,v.disk_reads,rank() over(order by v.disk_reads desc) elapsed_rank from v$sql v) a where elapsed_rank <= 10"
    // String[] fields=new String[]{"sqlId","sql_text","child_number","elapsed_time","cpu_time","disk_reads","elapsed_rank"};
    public static final String SqlIOSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.numeric.sql_id sqlId, sql.metrics.numeric.child_number child_number," +
            " sql.metrics.string.sql_text,sql.metrics.numeric.elapsed_time elapsed_time,sql.metrics.numeric.cpu_time cpu_time,sql.metrics.numeric.disk_reads disk_reads,sql.metrics.numeric.elapsed_rank elapsed_rank " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " order by \\\"@timestamp\\\" desc\",\"time_zone\":\"Asia/Shanghai\" '}'";

    //表空间中的已用空间比率  usage_rate  //oracle.tablespace.data_file.name tablespace,oracle.tablespace.data_file.size.free.bytes free,oracle.tablespace.data_file.size.bytes total
    public static final String TablespaceUsedPct="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute)  timestamp,sql.metrics.string.tablespace_name tablespace,sql.metrics.numeric.usage_rate pct" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " + //oracle
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,tablespace,pct \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //按文件名显示的平均数据文件大小 sql.metrics.string.tablespace_name
    public static final String AvgTablespaceSizePerNameSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.tablespace.data_file.name  tablespace,oracle.tablespace.data_file.size.bytes avgSize" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            "  group by timestamp,tablespace, avgSize \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //最大数据文件大小 -->无法获取表空间路径
    public static final String MaxFileSizeSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.tablespace.data_file.name  tablespace,oracle.tablespace.data_file.size.max.bytes maxSize" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,tablespace, maxSize \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //数据文件已用空间的比率
    public static final String UsedSpacePctSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.tablespace.data_file.name  tablespace,oracle.tablespace.space.used.bytes used,oracle.tablespace.space.total.bytes total" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and (oracle.tablespace.data_file.name is not null and oracle.tablespace.space.used.bytes is not null) "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  timestamp, tablespace ,used,total \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //归档日志每天增长量  ---找不到些指标
    public static final String ArchivedLogIncrementSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 1.0 day) as timestamp,name ,\\\"总容量\\\" total" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, name,total"+
            " order by \\\"@timestamp\\\" desc\" ,\"time_zone\":\"Asia/Shanghai\" '}'";
//    归档监控
//    归档目录使用情况
//    select distinct name,round(total_mb/1024) "总容量",
//    round(free_mb/1024) "空闲空间",
//    round((free_mb/total_mb)*100) "可用空间比例"
//    from gv$asm_diskgroup where name = 'ARCHIVE';

    //归档日志占用容量 ---找不到些指标
    public static final String ArchivedLogDataFileSizeSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10 second) as timestamp,name ,\\\"总容量\\\" total,\"可用空间比例\" free" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, total,free "+
            " order by \\\"@timestamp\\\" desc \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //监控锁  ---找不到些指标 --https://blog.csdn.net/e345ug/article/details/105992128
    public static final String MonitoringLockSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.username username,sql.metrics.string.osuser osuser,sql.metrics.string.schemaname schema, sql.metrics.numeric.sid SID,sql.metrics.numeric.serial# SERIAL,sql.metrics.string.row_wait_obj lockedObject,sql.metrics.string.machine app,sql.metrics.string.logon_time loginTime,sql.metrics.string.status status" + //不需要,sql.metrics.string.program， unlockSQL拼接alter system kill session 'sid,serail'
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and sql.metrics.string.oracle_metric_type='''lock'''"+
            " and service.address like ''{0}''"+
            " ''{1}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " order by \\\"@timestamp\\\" desc '}'";

    //慢查询  --不需要service.address connIP,
    public static final String SlowSql="'{'\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.sql_text sqlStr,sql.metrics.numeric.count executes,sql.metrics.numeric.total_time execDuration ,sql.metrics.numeric.avg_time avgExecDuration ,sql.metrics.string.first_load_time execTime,sql.metrics.string.last_load_time,sql.metrics.string.last_active_time latestActiveTime ,sql.metrics.string.user_name user" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and sql.metrics.string.oracle_metric_type='''slow'''"+
            " and service.address like ''{0}''"+
            " ''{1}''"+
            " and (sql.metrics.string.sql_text is not null or sql.metrics.numeric.count is not null or sql.metrics.numeric.total_time is not null or sql.metrics.string.first_load_time  is not null or sql.metrics.string.last_active_time is not null ) "+
//            " and sql.metrics.numeric.total_time>1000 "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " order by \\\"@timestamp\\\" desc \" ,\"time_zone\":\"Asia/Shanghai\" '}'"; //不能用这个,否则拿不到总数

    //查询job  ---找不到指标
    public static final String QueryJobSql="'{'\"query\": \"select \\\"@timestamp\\\", sql.metrics.numeric.string.job JOB, sql.metrics.numeric.string.log_user LOG_USER, sql.metrics.numeric.string.priv_user PRIV_USER ,sql.metrics.numeric.string.last_date LAST_DATE, sql.metrics.numeric.string.next_date NEXT_DATE, sql.metrics.numeric.string.next_sec NEXT_SEC, sql.metrics.numeric.string.total_time TOTAL_TIME, sql.metrics.numeric.numeric.broken BROKEN, sql.metrics.numeric.numeric.interval INTERVAL, sql.metrics.numeric.numeric.failures FAILURES, sql.metrics.numeric.string.what WHAT, sql.metrics.numeric.string.nls_env NLS_ENV, sql.metrics.numeric.string.misc_env MISC_ENV, sql.metrics.numeric.string.instance INSTANCE" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and sql.metrics.string.oracle_metric_type='''job'''"+
            " and service.address like ''{0}''"+
            " ''{1}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //Top10 按计算机划分的平均游标(TOP 10)
    public static final String AvgCursorPerHostSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.performance.machine machine,oracle.performance.cursors.avg avg " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and oracle.performance.cursors.avg is not null " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,machine,avg \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //Top10 按计算机列出的总游标数(TOP 10)
    public static final String TotalCursorNumPerHostSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp ,oracle.performance.machine machine,oracle.performance.cursors.total total " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and oracle.performance.cursors.total is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,machine, total \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //Top10 最大游标数（按计算机排列）(TOP 10)
    public static final String TotalCursorMaxSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.performance.machine machine, oracle.performance.cursors.max max " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp , machine,max \" ,\"time_zone\":\"Asia/Shanghai\" '}'";
    //总/实际解析游标
    public static final String TotalRealCursorParsedSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.performance.cursors.parse.real real,oracle.performance.cursors.parse.total total " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,real,total \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //当前打开的游标
    public static final String CurrentOpenCursorSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp,oracle.performance.cursors.opened.current open " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp, open \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //按缓冲池获取一致(TOP 10)
    public static final String BufferPoolGetConsistenceSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp,oracle.performance.buffer_pool pool,oracle.performance.cache.get.consistent consistence " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp ,pool,consistence limit 10 \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //数据库块按缓冲池 (TOP 10) //会话缓存命中数
    public static final String DBBlockCacheHitsSql ="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp,oracle.performance.buffer_pool pool,oracle.performance.cache.buffer.hit.pct hits " + //oracle.performance.cursors.session.cache_hits
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and oracle.performance.cache.buffer.hit.pct is not null"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp,pool ,hits limit 10 \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //会话缓存命中数命中率 //从游标缓存改为 会话缓存命中数命中率
    public static final String CacheHitsPctSql="'{'\"query\": \"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp,oracle.performance.cursors.session.cache_hits hitPct " + //oracle.performance.cursors.cache_hit.pct
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by timestamp ,hitPct limit 10 \" ,\"time_zone\":\"Asia/Shanghai\" '}'";

    //重新加载，锁定请求，固定请求
    public static final String ReloadLockedFixedRequestSql="'{'\"query\": \"select avg(oracle.performance.cache.buffer.hit.pct) bufferHit,avg(oracle.performance.cursors.cache_hit.pct) cursorHit , avg(oracle.performance.io_reloads) reload,  avg(oracle.performance.lock_requests) lock, avg(oracle.performance.pin_requests) pin" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''oracle''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //系统内存,SGA/PGA内存使用率 --2023.02.27新增
    public static final String SystemSgaPgaMemoryUsageSql ="'{'\"query\": \"select sql.metrics.string.oracle_metric_type type, sql.metrics.numeric.pctused pctused, sql.metrics.numeric.total total" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //数据坏块 2023.02.27新增owner type segment_name partition_name  file start_block  end_block block_corrupted description
    public static final String CorruptedBlock="'{'\"query\": \"select sql.metrics.string.owner owner, sql.metrics.string.corr_start_block start_block, sql.metrics.string.corr_end_block end_block, " +
            " sql.metrics.string.segment_type type , sql.metrics.string.segment_name segment_name,sql.metrics.string.partition_name partition_name, \\\"sql.metrics.string.file#\\\" file,sql.metrics.numeric.blocks_corrupted block_corrupted, sql.metrics.string.description description "+
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''sql''' " +
            " and service.address like ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " {3} \",\"time_zone\":\"Asia/Shanghai\" '}'";
}
