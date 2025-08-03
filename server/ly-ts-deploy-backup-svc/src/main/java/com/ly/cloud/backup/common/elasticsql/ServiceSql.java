package com.ly.cloud.backup.common.elasticsql;




/**
 * 全链路-服务sql
 * */
public class ServiceSql {

    public static final String serviceLatencyAndTmpInSql = "'{'\"query\": \"select service.name ," +
            "sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " FROM \\\"apm-*-transaction\\\" where UCASE(service.name) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by service.name  limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数）
     */
    public static final String serviceLatencyAndTmpAllSql = "'{'\"query\": \"select " +
            "sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数）
     */
    public static final String serviceLatencyAndTmpAllByServiceSql = "'{'\"query\": \"select " +
            "sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) = ''{1}'' " +
//            " FROM \\\"apm-*-transaction\\\" where UCASE(service.name) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数）
     */
    public static final String serviceLatencyAndTmpGroupByServiceNameSql = "'{'\"query\": \"select " +
            " service.name,sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\"" +
            " where \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by  service.name \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数）
     */
    public static final String serviceLatencyGroupByServiceNameSql = "'{'\"query\": \"select " +
            " service.name,sum(transaction.duration.us)/count(1) latencyAvg " +
            " FROM \\\"apm-*-transaction\\\"" +
            " where \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by  service.name \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足条件的服务的错误请求数
     */
    public static final String serviceErrorRequestAllSql = "'{'\"query\": \"select " +
            " count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\" where ucase(service.name) in {1} " +
            " and event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 通过服务英文名称获取满足条件的服务的错误请求数()
     */
    public static final String serviceErrorRequestAllByServiceSql = "'{'\"query\": \"select " +
            " count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) = ''{1}'' " +
            " and event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足条件的服务的错误请求数
     */
    public static final String serviceErrorRequestGroupByServiceNameSql = "'{'\"query\": \"select " +
            " service.name,count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\" where  " +
            "  event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by service.name  \",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数）
     */
    public static final String serviceLatencyAndTmpChartGroupByServiceNameSql = "'{'\"query\": \"select" +
            " service.name,histogram(\\\"@timestamp\\\",interval {3} minute) as timestamp," +
            " sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" " +
            " where \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by service.name,timestamp  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数） IN
     */
    public static final String serviceLatencyAndTmpChartSql = "'{'\"query\": \"select" +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" " +
            "where ucase(service.name) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的服务的属性（延迟、吞吐量、请求数） IN
     */
    public static final String serviceLatencyAndTmpChartByServiceSql = "'{'\"query\": \"select" +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " sum(transaction.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" " +
            "where lcase(service.name) = ''{1}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 根据获取服务名称获取容器ID
     */
    public static final String getContainerByServiceNameSql = "'{'\"query\": \"select " +
            " container.id" +
            " from \\\"apm-*-transaction\\\" " +
            " where ucase(service.name) = ucase(''{0}'') " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " limit 10 \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 根据获取服务名称获取容器ID
     */
    public static final String getContainerByServiceNameNoTimeSql = "'{'\"query\": \"select " +
            " container.id" +
            " from \\\"apm-*-transaction\\\" " +
            " where ucase(service.name) = ucase(''{0}'') " +
            " limit 10 \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 详情-概览-吞吐量-查询
     */
    public static final String getTransactionTpmAvgSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timeFrame," +
            " count(1)/ {3} as tpm"+
            " from \\\"apm-*-transaction\\\" " +
            " where lcase(service.name) = ''{0}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timeFrame \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 详情-概览-吞吐量-查询
     */
    public static final String getTransactionTpmAvgByHealthStatusSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timeFrame," +
            " count(1)/ {3} as tpm"+
            " from \\\"apm-*-transaction\\\" " +
            " where lcase(service.name) = ''{0}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timeFrame " +
            " having tpm >= {4}"+
            "\",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     * 详情-概览-请求数-查询
     */
        public static final String getTransactionRequestSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timeFrame," +
            " count(1)/ {3} as tpm"+
            " from \\\"apm-*-transaction\\\" " +
            " where lcase(service.name) = ''{0}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timeFrame \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 按照时间线获取服务的错误请求数
     */
    public static final String getServiceErrorRequestByServiceSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timestamp," +
            " count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) = ''{0}'' " +
            " and event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 按照时间线获取服务的错误请求数
     */
    public static final String getServiceErrorRequestByServicesSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timestamp," +
            " count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) in {0} " +
            " and event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 按照时间线获取服务的错误请求数
     */
    public static final String getServiceErrorRequestGroupByServiceNameSql = "'{'\"query\": \"select " +
            " service.name,histogram(\\\"@timestamp\\\",interval {2} minute) as timestamp," +
            " count(1) as errorRequestCount" +
            " FROM \\\"apm-*-transaction\\\"  " +
            " where event.outcome = ''failure''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp,service.name \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     *  按照时间线获取服务的请求数
     */
    public static final String getServiceRequestTotalSql = "'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {3} minute) as timestamp," +
            " count(1) as requestTotal" +
            " FROM \\\"apm-*-transaction\\\" where lcase(service.name) = ''{0}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取满足件的来源资源的属性（延迟、吞吐量、请求数）
     */
    public static final String resourceLatencyAndTmpChartSql = "'{'\"query\": \"select" +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " sum(span.duration.us)/count(1) latencyAvg ,count(1)/ {0} as tpm" +
            " ,count(1) as requestTotal" +
            " FROM \\\"apm-*-span\\\" " +
            " where span.destination.service.resource is not null" +
            " and ucase(span.destination.service.resource) in {1} " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  group by timestamp  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 计算一个服务最近时间的总吞吐量tpm
     */
    public static final String getServiceTpmSql = "'{'\"query\": \"select" +
            " count(1)/ {0} as tpm" +
            " FROM \\\"apm-*-transaction\\\" " +
            " where " +
            " lcase(service.name) = ''{1}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  \",\"time_zone\":\"Asia/Shanghai\"'}'";



    /**
     * 计算一个服务最近时间的总吞吐量tpm
     */
    public static final String getSpanTpmSql = "'{'\"query\": \"select" +
            " count(1)/ {0} as tpm" +
            " FROM \\\"apm-*-span\\\" " +
            " where lcase(service.name) = ''{4}'' and " +
            " lcase(span.destination.service.resource) = ''{1}'' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            "  limit 1\",\"time_zone\":\"Asia/Shanghai\"'}'";

//    {"query": "select count(1)/15.0 as tpm FROM \"apm-*-transaction\" where LCASE(service.name)=LCASE('LY-GATEWAY-SERVER-SVC-LYJW') and \"@timestamp\">=DATETIME_PARSE('2023-04-03 17:34:12', 'yyyy-MM-dd HH:mm:ss') and \"@timestamp\" <=DATETIME_PARSE('2023-04-03 17:49:12', 'yyyy-MM-dd HH:mm:ss')    ","time_zone":"Asia/Shanghai"}

}
