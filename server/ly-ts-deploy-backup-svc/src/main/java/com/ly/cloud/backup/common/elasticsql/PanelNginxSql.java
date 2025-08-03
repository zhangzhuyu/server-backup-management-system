package com.ly.cloud.backup.common.elasticsql;

/**
 *  监控面板- nginx 面板sql
 *  https://www.dandelioncloud.cn/article/details/1458117192020746241
 *  https://blog.csdn.net/yangkei/article/details/126934593
 *  注:nginx图表需要开启stub_status功能才能采集到相关的指标到metricbeat--上面是2023.02.27在网上搜索到的，先在192.168.35.202上试验看行不行
 * */
public class PanelNginxSql {
    //Nginx ip状态 ---未找到状态指标
    public static String StatusIPNginxSql="{\"query\":\"select service.address ip" + //\"@timestamp\", , status
            " from \\\"metricbeat-*\\\" " +
            " where event.module='nginx' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by  ip "+ //\"@timestamp\", , status
            "  \",\"time_zone\":\"Asia/Shanghai\" }"; //order by \"@timestamp\" desc

    //概览 异常日志，日志总数，当前连接数，最大连接数 ---日志是不是在apm里?
    public static String SummaryNginxSql="'{'\"query\":\"select \\\"@timestamp\\\",service.address ip, nginx.stubstatus.active currentConn,nginx.stubstatus.accepts maxConn" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and host.ip = ''{0}''"+
            " and (nginx.stubstatus.active is not null or nginx.stubstatus.accepts is not null)"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String SummaryNginxLogSql="'{'\"query\":\"select  count(1) totalLog, count(event.type='''error'''OR NULL) abnormalLog" +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and host.ip = ''{0}''"+
            " and event.dataset = ''nginx.access''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            "\",\"time_zone\":\"Asia/Shanghai\" '}'";

    //活动连接折线图 --单折线带告警线
    public static String ActiveConnNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,nginx.stubstatus.active activeConn" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and host.ip = ''{0}''"+
            //" and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,activeConn "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //请求速率折线图 --单折线带告警线
    public static String RequestRateNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,nginx.stubstatus.requests requests" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,requests "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String RequestRateNginxQueryDsl="" +
            "{\n" +
            "  \"size\": 0,\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "      {0}" +
            "          {\"match\": { \"event.dataset\": \"nginx.stubstatus\" }},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"aggs\": {\n" +
            "    \"linevalues\": {\n" +
            "      \"date_histogram\": {\n" +
            "        \"field\": \"@timestamp\",\n" +
            "        \"calendar_interval\": \"minute\"\n" +
            "      },\n" +
            "      \"aggs\": {\n" +
            "        \"values\": {\n" +
            "          \"avg\": {\n" +
            "            \"field\": \"nginx.stubstatus.requests\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"values_deriv\": {\n" +
            "          \"derivative\": {\n" +
            "            \"buckets_path\": \"values\" \n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";

    //掉落率折线图 --单折线带告警线
    public static String DropRateNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '60' second) timestamp,nginx.stubstatus.dropped dropped" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and host.ip = ''{0}''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,dropped "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //接受和处理速率折线图
    public static String AcceptHandleRateNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,nginx.stubstatus.handled handled" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,handled "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String AcceptHandleRateNginxQueryDsl="" +
            "{\n" +
            "  \"size\": 0,\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.stubstatus\" }},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"aggs\": {\n" +
            "    \"handled\": {\n" +
            "      \"date_histogram\": {\n" +
            "        \"field\": \"@timestamp\",\n" +
            "        \"calendar_interval\": \"minute\"\n" +
            "      },\n" +
            "      \"aggs\": {\n" +
            "        \"values\": {\n" +
            "          \"avg\": {\n" +
            "            \"field\": \"nginx.stubstatus.handled\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"values_deriv\": {\n" +
            "          \"derivative\": {\n" +
            "            \"buckets_path\": \"values\" \n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"accepts\": {\n" +
            "      \"date_histogram\": {\n" +
            "        \"field\": \"@timestamp\",\n" +
            "        \"calendar_interval\": \"minute\"\n" +
            "      },\n" +
            "      \"aggs\": {\n" +
            "        \"values\": {\n" +
            "          \"avg\": {\n" +
            "            \"field\": \"nginx.stubstatus.accepts\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"values_deriv\": {\n" +
            "          \"derivative\": {\n" +
            "            \"buckets_path\": \"values\" \n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";

    //读取/写入/等待折线图
    public static String ReadWriteWaitRateNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '60' second) timestamp,nginx.stubstatus.reading reading, nginx.stubstatus.writing writing, nginx.stubstatus.waiting waiting " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp,reading ,writing ,waiting "+
            " order by timestamp  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //一段时间内的响应代码直方图  --- 指标http.status.code不知道是不是对的
    public static String StatusCodeHistogramNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second),http.response.code code" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, code "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String StatusCodeHistogramNginxQueryDSL="" +
            "{\n" +
            "  \"size\": 0,\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.access\" }},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"aggs\": {\n" +
            "    \"linevalues\": {\n" +
            "      \"date_histogram\": {\n" +
            "        \"field\": \"@timestamp\",\n" +
            "        \"calendar_interval\": \"minute\"\n" +
            "      },\n" +
            "      \"aggs\": {\n" +
            "        \"values\": {\n" +
            "          \"terms\": {\n" +
            "            \"field\": \"http.response.status_code\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";

    //随着时间的推移出现错误直方图 --- Sql语句无法正常工作 2023.1.13 ---改成DSL
    public static String ErrorByTimeNginxSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) , error.message error" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and server.address like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String ErrorByTimeNginxQueryDSL="" +
            "{\n" +
            "  \"size\": 0,\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.error\"}},\n" +
//            "          {\"match\": {\"host.os.codename\":\"buster\"}},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"aggs\": {\n" +
            "    \"linevalues\": {\n" +
            "      \"date_histogram\": {\n" +
            "        \"field\": \"@timestamp\",\n" +
            "        \"calendar_interval\": \"minute\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";

    //以下是nginx dashboard 中的Nginx logs overview | Nginx access and error logs

    //首页(top page应译为被top10访问的页面的url) 条形图 --2023.02.10找到filebeat指标nginx.access.url(url.original)
    public static String IndexPageNginxSql="'{'\"query\":\"select url.original url,count(1) co" + //nginx.access.url,\"@timestamp\" timestamp,
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and host.ip = ''{0}''"+
            " and event.dataset = ''nginx.access''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by url.original "+
            " order by co desc LIMIT 10 \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //https://www.elastic.co/guide/en/beats/filebeat/current/exported-fields-nginx.html#_error_5
    //数据卷折线图  ---2023.02.10按elastic dashboard页面的http.response.body.bytes的sum找到(nginx.access.body_sent.bytes)
    public static String DataVolumeNginxSql ="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '60' second) timestamp,http.response.body.bytes volume" +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and host.ip = ''{0}''"+
            " and event.dataset = ''nginx.access''"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, volume "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //操作系统饼图  -- --2023.02.10 找到filebeat指标 nginx.access.user_agent.os_name /nginx.access.user_agent.os
    public static String OsPieChartNginxSql="'{'\"query\":\"select \\\"@timestamp\\\",user_agent.os.name os, user_agent.os.full_name name"+//nginx.access.user_agent.os_name ,nginx.access.user_agent.os" +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by \\\"@timestamp\\\",os,name "+
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String OsBrowserQueryDSL="\n" +
            "{\n" +
            "  \"size\": 0,\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.access\"}},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"aggs\": {\n" +
            "    \"os\": {\n" +
            "      \"terms\": {\n" +
            "        \"field\": \"user_agent.os.name\"\n" +
            "      }\n" +
            "      , \"aggs\": {\n" +
            "        \"osversion\": {\n" +
            "          \"terms\": {\n" +
            "            \"field\": \"user_agent.os.version\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"browser\": {\n" +
            "      \"terms\": {\n" +
            "        \"field\": \"user_agent.name\"\n" +
            "      }\n" +
            "      , \"aggs\": {\n" +
            "        \"osversion\": {\n" +
            "          \"terms\": {\n" +
            "            \"field\": \"user_agent.version\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}" +
            "";

    //浏览器细分饼图 -- 2023.02.10找到filebeat指标nginx.access.agent
    public static String BrowserPieChartNginxSql="'{'\"query\":\"select \\\"@timestamp\\\",user_agent.original browser,user_agent.device.name version" + //nginx.access.agent
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
            " and server.address like ''{0}'' "+
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by \\\"@timestamp\\\",browser,version "+
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //一段时间内的访问日志折线图 -- 找到指标nginx.error.message 但不用Sql查,无法聚合
    public static String RequestLogHistogramNginxSql="";//""'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second),message " +
//            " from \\\"filebeat-*\\\" " +
//            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
//            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
//            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " group by timestamp "+
//            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //Nginx 错误日志---列表 timestamp/errorType/log ngnix.error.message
    public static String ErrorLogNginxSql="'{'\"query\":\"select \\\"@timestamp\\\",log.level,message " +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
            " and message is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " LIMIT {3},{4} order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String ErrorLogNginxQueryDSL="\n" +
           "{\n" +
            "  \"from\": {3},\n" +
            "  \"size\": {4},\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.error\"}},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  }\n" +
            "}"+
            "";


    //Nginx 访问日志 --列表 timestamp/url.original/http.request.method/http.response.status.code/http.response.body.bytes  ---2023.1.11运筹给的，暂时没数据nginx.access.url ,nginx.access.method,nginx.access.response_code,nginx.access.body_sent.bytes
    public static String AccessLogNginxSql ="'{'\"query\":\"select \\\"@timestamp\\\" timestamp,url.original urlOriginal,http.request.method httpRequestMethod,http.response.status_code httpResponseStatusCode,http.response.body.bytes httpResponseBodyBytes " +
            " from \\\"filebeat-*\\\" " +
            " where event.module='''nginx''' " +
//            " and server.address like ''{0}'' "+
//            " and url.original is not null "+
//            " and container.id='''access.log''' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '1' MINUTE "+
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String AccessLogNginxQueryDSL="\n" +
            "{\n" +
            "  \"from\": {3},\n" +
            "  \"size\": {4},\n" +
            "  \"query\": {\n" +
            "    \"bool\": {\n" +
            "        \"must\": [\n" +
            "          {\"match\": { \"event.dataset\": \"nginx.access\"}},\n" +
            "          {\"range\": {\"@timestamp\": {\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\"time_zone\": \"+08:00\",\"gte\": \"{1}\", \"lte\": \"{2}\"}}}\n" +
            "       ]\n" +
            "    }\n" +
            "  }\n" +
            "}"+
            "";

}


//event.module :"nginx" and container.id:"access.log" and  http.request.referrer:"http://192.168.35.202:15678/" 这个是用filebeat收集的nginx反代的应用日志，星哥你的Nginx面板看看有没有需要的
//|nginx.stubstatus.accepts|nginx.stubstatus.active|nginx.stubstatus.current|nginx.stubstatus.dropped|nginx.stubstatus.handled|nginx.stubstatus.hostname|nginx.stubstatus.reading|nginx.stubstatus.requests|nginx.stubstatus.waiting|nginx.stubstatus.writing|