package com.ly.cloud.backup.common.elasticsql;


/**
 * 链路追踪APM的SQL
 */
public class ApmSql {

    /**
     * 获取网关采集到的request请求体信息
     */
    public static final String gatewayRequestInfoSql = "'{'\"query\": \"select *" +
            " FROM \\\"apm-*-transaction\\\" where ucase(service.name) = ''{0}'' " +
            " and trace.id=''{3}''" +
            " and labels.requestHeaderNames_cookie != ''''"+
//            " and span.type = '''' "+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     * 获取网关采集到的request请求体信息
     */
    public static final String gatewayRequestInfoByTransactionNameSql = "'{'\"query\": \"select *" +
            " FROM \\\"apm-*-transaction\\\" where transaction.name = ''{0}'' " +
            " and trace.id=''{3}''" +
            " and labels.requestHeaderNames_cookie != ''''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 获取网关采集到的request请求体信息
     */
    public static final String gatewayRequestInfoByTraceIdSql = "'{'\"query\": \"select *" +
            " FROM \\\"apm-*-transaction\\\" where " +
            "trace.id=''{3}'' " +
            " and labels.requestHeaderNames_cookie != ''''"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " limit 1  \",\"time_zone\":\"Asia/Shanghai\"'}'";

}
