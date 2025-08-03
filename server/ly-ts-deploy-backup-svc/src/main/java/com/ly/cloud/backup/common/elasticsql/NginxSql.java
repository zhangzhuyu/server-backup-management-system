package com.ly.cloud.backup.common.elasticsql;

public class NginxSql {
    public static final String StatisticsInterfaceSQL="'{'\"query\":" +
            "\" select histogram(\\\"@timestamp\\\",interval '60' second) as \\\"time\\\"," +
            "http.request.referrer,count(1) as count FROM \\\"filebeat-*\\\" " +
            " where event.module = '''nginx''' and agent.hostname = ''{2}'' and host.ip = ''{3}'' and " +
//        " where  " +
            " http.response.status_code is not null and http.request.referrer is not null " +
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            " group by \\\"time\\\",http.request.referrer " +
            " order by count desc \",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String StatusCodeInterface="'{'\"query\":" +
            "\"select http.response.status_code,count(1) as count,sum(1) as sum FROM \\\"filebeat-*\\\" " +
        " where event.module = '''nginx''' and agent.hostname = ''{2}'' and host.ip = ''{3}'' and " +
//        " where  " +
            " http.response.status_code is not null " +
            "and \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'') and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            "group by http.response.status_code\",\"time_zone\":\"Asia/Shanghai\"'}'";
    public static final String BrowserTypeInterface="'{'\"query\":" +
            "\"select http_user_agent,count(1) as count FROM \\\"filebeat-*\\\" where container.image.name = ''nginx:v1''" +
            " and http.response.status_code is not null " +
            "and \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'') and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            "group by http_user_agent\",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String timeSpanStatistics="'{'\"query\":" +
            "\"select histogram(\\\"@timestamp\\\",interval '60' second) as \\\"time\\\"," +
            "    count(*) as count FROM \\\"filebeat-*\\\"" +
            " where  " +
            "  \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            " group by \\\"time\\\"\",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String streamStatistics="'{'\"query\":" +
            "\"select url_domain,sum(cast(body_bytes_sent as Integer) ) as \\\"sum\\\",sum(iif(http.response.status_code=''200'',1,0)) as success, sum(iif(http.response.status_code=''200'',0,1)) as error FROM \\\"filebeat-*\\\" where container.image.name = ''nginx:v1''" +
            " and http.response.status_code is not null " +
            "and \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'') and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            "group by url_domain\",\"time_zone\":\"Asia/Shanghai\"'}'";

    public static final String TPMStatistics ="'{'\"query\":" +
            "\"select histogram(\\\"@timestamp\\\",interval '60' second) as \\\"time\\\"," +
            "    count(iif(http.response.status_code=''200'',1,0)) as count FROM \\\"filebeat-*\\\" where container.image.name = ''nginx:v1''" +
            " and http.response.status_code is not null " +
            "and \\\"@timestamp\\\">= DATETIME_PARSE(''{0}'', ''yyyy-MM-dd HH:mm:ss'') and \\\"@timestamp\\\"<= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'') "+
            "group by \\\"time\\\"\",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 根据nginx的IP地址和反向代理的应用地址，查询请求状态（成功：200，失败：除了200以外）
     */
    public static final String requestStatistics="'{'\"query\": \"select host.ip_str,http.response.status_code,count(*) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by host.ip_str,http.response.status_code \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 访问数，即Login请求的执行次数
     */
    public static final String loginInterfaceCount ="'{'\"query\": \"select host.ip_str,count(*) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and url.original like '''%'{4}'%'''  " +
//            " and url.original like '''%'{4}'%'''  " +
            " group by host.ip_str,http.response.status_code \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 统计不同段时间的登录用户数（login数量）
     */
    public static final String nginxFlowAnalyUserCount="'{'\"query\": \"select " +
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and url.original like '''%'{5}'%'''  " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     *nginx应用流量_统计不同时间的访问数(即Login请求的执行次数)
     */
    public static final String nginxFlowAnalysisVisits ="'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {5} minute) as timestamp,"+
            " host.ip_str,count(*) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and url.original like '''%'{4}'%'''  " +
//            " and suricata.eve.http.url like '''%'{4}'%'''  " +
            " group by timestamp,host.ip_str\",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     *nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalysisIpCount ="'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp,"+
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timestamp,host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";


    /**
     * nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalyQpmCount="'{'\"query\":" +
            " \"select histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timestamp,host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalyHighFrequency="'{'\"query\":" +
            " \"select histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp," +
            " url.original,"+
//            " suricata.eve.http.url,"+
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by url.original,timestamp,host.ip_str," +
//            " group by suricata.eve.http.url,timestamp,host.ip_str," +
            " host.ip_str order by count desc  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalyHighFrequencyTop="'{'\"query\":" +
            " \"select " +
            " url.original,"+
//            " suricata.eve.http.url,"+
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by url.original,host.ip_str," +
//            " group by suricata.eve.http.url,timestamp,host.ip_str," +
            " host.ip_str order by count desc limit 10 \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 流量分析_统计不同时间段的客户端请求类型
     */
    public static final String nginxFlowAnalyClientRequestType = "'{'\"query\":" +
            " \"select " +
            " user_agent.name,"+
//            " traefik.access.user_agent.name,"+
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by user_agent.name,host.ip_str," +
//            " group by traefik.access.user_agent.name,host.ip_str," +
            " host.ip_str order by count desc  \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 流量分析_统计不同时间段的客户端请求类型
     */
    public static final String nginxFlowAnalyClientSource = "'{'\"query\":" +
            " \"select " +
            " user_agent.os.full,"+
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by user_agent.os.full,host.ip_str," +
            " host.ip_str order by count desc \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 流量分析_统计不同时间段的请求状态码
     */
    public static final String nginxFlowAnalyRequestStatusCode = "'{'\"query\":" +
            " \"select " +
            " http.response.status_code,"+
//            " suricata.eve.http.status,"+
            " count(*) as count " +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by http.response.status_code,host.ip_str," +
            " host.ip_str order by count desc \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     *nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalyIpCount="'{'\"query\": \"select " +
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";

/*    *//**
     * 统计不同段时间的登录用户数（login数量）
     *//*
    public static final String nginxFlowAnalyUserCount="'{'\"query\": \"select " +
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and url.original like '''%'{5}'%'''  " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";*/

    /**
     *nginx应用流量_统计不同时间的IP数（即IP数量去重的结果）
     */
    public static final String nginxFlowAnalyIpGroupByTime="'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp,"+
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timestamp,host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 统计登录用户数（login数量）
     */
    public static final String nginxFlowAnalyUserCountGroupByTime="'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {4} minute) as timestamp,"+
            " host.ip_str,count(distinct source.ip) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and url.original like '''%'{5}'%'''  " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by timestamp,host.ip_str \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     * 访问数，即Login请求的执行次数
     */
    public static final String loginIntefaceCount="'{'\"query\": \"select host.ip_str,count(*) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and url.original like '''%'{4}'%'''  " +
            " group by host.ip_str,http.response.status_code \",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     *nginx应用流量_统计不同时间的访问数(即Login请求的执行次数)
     */
    public static final String nginxFlowAnalyVisits="'{'\"query\": \"select " +
            " histogram(\\\"@timestamp\\\",interval {5} minute) as timestamp,"+
            " host.ip_str,count(*) as count" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and url.original like '''%'{4}'%'''  " +
            " group by timestamp,host.ip_str\",\"time_zone\":\"Asia/Shanghai\"'}'";

    /**
     *nginx应用流量_根据应用域名获取nginx请求分析列表信息
     */
    public static final String nginxFlowAnalyRequestAnaly="'{'\"query\": \"select " +
            " suricata.eve.http.length,suricata.eve.http.status,suricata.eve.http.http_method, " +
            " http.version,http.response.body.bytes,suricata.eve.alert.action,suricata.eve.http.url,suricata.eve.http.http_refer" +
            " from \\\"filebeat-*\\\" where host.ip_str like '''%'{0}'%''' " +
            " and event.module=''nginx'' and container.id=''access.log'' and " +
            " http.request.referrer like '''%'{1}'%''' " +
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{3}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " \",\"time_zone\":\"Asia/Shanghai\"'}'";


}
