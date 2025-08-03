package com.ly.cloud.backup.common.elasticsql;

/**
 * 监控面板---主机类的sql
 * */
public class PanelHostSql {
    //主机ip,状态
    public static final String StatusIPsHostSql="{\"query\": \"select host.ip_str ip " + //\"@timestamp\",
            " from \\\"metricbeat-*\\\" " +
            " where event.module='system' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  ip  "+ //\"@timestamp\",
            "  \",\"time_zone\":\"Asia/Shanghai\" }"; //order by \"@timestamp\" desc

    //Disk Utilization per Device磁盘利用率 2023.1.11 10：42验证有值
    public static final String DiskUtilizationPerDeviceSql ="'{'\"query\": \"select \\\"@timestamp\\\",system.filesystem.device_name device,system.filesystem.used.pct pct" +
            " from \\\"metricbeat-*\\\" " +
            " where event.dataset='''system.filesystem''' and host.ip_str like ''{0}''  " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by  \\\"@timestamp\\\",system.filesystem.device_name,system.filesystem.used.pct " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"'}'";
    //硬盘io按分区 filesystem.device_name
    public static final String DiskIOsPerDeviceSql= "'{'\"query\": \"select \\\"@timestamp\\\",system.diskio.name device,system.diskio.read.count read, system.diskio.write.count write" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''system''' and host.ip_str like ''{0}''  " + //.filesystem
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by  \\\"@timestamp\\\",device,read,write " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"'}'";
    //吞叶量
    public static final String DiskThroughputPerDeviceSql="'{'\"query\": \"select \\\"@timestamp\\\",system.diskio.name device,system.diskio.read.bytes read, system.diskio.write.bytes write" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''system''' and host.ip_str like ''{0}''  " + //.filesystem
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by  \\\"@timestamp\\\",device,read,write " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    //网络
    public static final String NetworkTrafficSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 5.0 minute) as timestamp, system.network.name network, \\\"system.network.in.packets\\\" inpackets ,system.network.out.packets outpackets" +
            " from \\\"metricbeat-*\\\" " +
            "where event.module = '''system''' " + //.network
            "and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            "group by timestamp, network,inpackets,outpackets order by timestamp desc\",\"time_zone\":\"Asia/Shanghai\"'}'";

    //注意带有in的字段要用\\\"括起来，否则会解析成sql的保留字
    public static final String NetworkReceivedSql="'{'\"query\":  \"select histogram(\\\"@timestamp\\\",interval 5.0 minute) as timestamp, system.network.name network, \\\"system.network.in.bytes\\\" inByte " +
            "from \\\"metricbeat-*\\\" " +
            "where event.module = '''system''' " + //.network
            "and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            "group by timestamp, system.network.name, \\\"system.network.in.bytes\\\" order by timestamp desc\",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String NetworkTransmittedSql="'{'\"query\":  \"select histogram(\\\"@timestamp\\\",interval 5.0 minute) as timestamp, system.network.name network, system.network.out.bytes out " +
            "from \\\"metricbeat-*\\\" " +
            "where event.module = '''system''' " + //.network
            "and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            "group by timestamp, network, out order by timestamp desc\",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String NetstatSql="'{'\"query\":  \"select histogram(\\\"@timestamp\\\",interval 10 second) as timestamp, \\\"system.socket.summary.tcp.all.established\\\" established " +
            " from \\\"metricbeat-*\\\" " +
            "where event.module = '''system''' " + //.network
//            "where event.dataset = '''system.socket.summary'''" +
            " and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, \\\"system.socket.summary.tcp.all.established\\\"  order by timestamp desc\",\"time_zone\":\"Asia/Shanghai\"'}'";

    //负载 环比用两条，与平均负载用同一条Sql
    public static final String LoadSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp, avg(\\\"system.load.15\\\") load" +
            " from \\\"metricbeat-*\\\" " +
            " where event.dataset = '''system.load''' " +
            " and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp order by timestamp desc\" ,\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String LoadAverageSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp, avg(\\\"system.load.1\\\") load1, avg(\\\"system.load.5\\\") load5, avg(\\\"system.load.15\\\") load15, avg(\\\"system.load.cores\\\") loadcores" +
            " from \\\"metricbeat-*\\\" " +
            " where event.dataset = '''system.load''' " +
            " and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp order by timestamp desc\",\"time_zone\":\"Asia/Shanghai\" '}'";

    //其它
//    public static final String ContextSwitchesSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10.0 minute) as timestamp, avg(\\\"system.load.1\\\"), avg(\\\"system.load.5\\\"), avg(\\\"system.load.15\\\"), avg(\\\"system.load.cores\\\")" +
//            " from \\\"metricbeat-*\\\" " +
//            " where event.dataset = '''system.load''' " +
//            " and host.ip_str like ''{0}'' " +
//            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
//            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " group by timestamp order by timestamp desc\" '}'";

    public static final String UDPStatsSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10.0 second) as timestamp, \\\"system.socket.summary.udp.all.count\\\" " +
            " from \\\"metricbeat-*\\\" " +
            " where event.dataset = '''system.socket.summary''' " + //.socket
            " and host.ip_str like ''{0}'' " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp ,\\\"system.socket.summary.udp.all.count\\\" order by timestamp desc\" ,\"time_zone\":\"Asia/Shanghai\"'}'";

    public static void main(String[] args){
        double bi=3.3566621696E10;
        System.out.println(bi/1024/1024/1024);
    }

/*    *//**
     * 详情-概览-服务器信息-延迟、吞吐量、请求总量
     *//*
    public static String getTransactionServerSql(String time,String serviceName,String startTime,String endTime,String traceId) throws ParseException {
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        //单位默认为小时
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" minute) as timestamp1," +
                " host.name,container.id,service.name,container.id,service.node.name," +
                " sum(transaction.duration.us)/count(1) as latencyAvg," +
                " count(1)/"+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" as tpm,count(1) as requestTotal " +
                " from \\\"apm-*-transaction\\\"" +
                " where 1=1" +
                " and LCASE(service.name)=LCASE('%s')"+
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')"
                +traceIdWhere+
                " group by timestamp1,host.name,container.id,service.name,container.id,service.node.name" +
                " order by timestamp1 desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }*/

    /**
     * 获取满足件的来源资源的属性（延迟、吞吐量、请求数）
     */
    public static final String serverLatencyAndTmpChartSql = "'{'\"query\": \"select" +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " host.name,container.id,service.name,service.node.name,"+
            " sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm," +
            " count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" " +
            " where ucase(service.name) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp,host.name,container.id,service.name,service.node.name \",\"time_zone\":\"Asia/Shanghai\"'}'";

}
