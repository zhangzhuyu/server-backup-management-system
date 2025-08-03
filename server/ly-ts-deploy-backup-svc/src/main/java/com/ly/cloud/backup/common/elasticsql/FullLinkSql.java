package com.ly.cloud.backup.common.elasticsql;

import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.enums.TimeEnums;
import com.ly.cloud.backup.util.DateUtil;
import com.ly.cloud.backup.util.UsualUtil;

import java.text.ParseException;
import java.util.*;

import static com.ly.cloud.backup.common.constant.CommonConstant.SIMPLE_DATE_FORMAT_STR;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;


/**
 * 全链路 ES 公共SQL语句
 */
public class FullLinkSql {

    /**
     * 计算两个日期【日期类型】之间的时间差值（按分钟计算），然后计算分组统计的方式【chart图的x左边的时间线间隔】
     */
    public static double getTimeLength(int time, String startTime, String endTime) throws ParseException {
        double timeLength = 0.0F;
        if (TimeEnums.CUSTOM_TIME.getCode().equals(time) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
           //求时间差，按分钟统计
            timeLength=DateUtil.getTimeLength(startTime,endTime);
            //年按月计算计算(按照apm的标准，年按每天1个点)
            if (timeLength>TimeEnums.ONE_YEAR.getValue()){
                return TimeEnums.ONE_YEAR.getGroupingMethod();
            }
            //月按周计算(按照apm的标准，月按每天1个点)
           if (timeLength>TimeEnums.ONE_MONTH.getValue()){
               return TimeEnums.ONE_MONTH.getGroupingMethod();
            }
            //周按天计算(按照apm的标准，周按每小时1个点)
            if (timeLength>TimeEnums.ONE_WEEK.getValue()){
                return TimeEnums.ONE_WEEK.getGroupingMethod();
            }
            //天按小时计算(按照apm的标准，天按每10分钟1个点)
            if (timeLength>TimeEnums.TWENTY_FOUR_HOUR.getValue()){
                return TimeEnums.TWENTY_FOUR_HOUR.getGroupingMethod();
            }else {
                //剩下小时按分钟计算
                return TimeEnums.FIFTEEN_MINUTE.getGroupingMethod();
            }
        }else {
            return TimeEnums.getGroupingMethod(time);
        }
    }

    /**
     * 计算两个日期【日期类型】之间的时间差值（按分钟计算）【开始和结束时间求差值，时间间隔】
     */
    public static String getTimeMinuteLength(String startTime, String endTime) throws ParseException {
        //求时间差，按分钟统计
        //这里得用string类型(1000)，不能用double,double会被附加逗号,(1,000)
        return UsualUtil.getString(DateUtil.getTimeLength(startTime,endTime));
    }

    /**
     * 查询错误日志方法
     * @param service 服务名称
     * @return
     */
    public static String getErrorCountSql(String service,String startTime,String endTime){
        String errorLogSql=" {\"query\": \" select error.grouping_key,count(1) error_time," +
                " last(\\\"@timestamp\\\",timestamp.us) last_timestamp,last(error.grouping_name,timestamp.us) name " +
                " from \\\"apm-*-error\\\" where LCASE(service.name)= LCASE('"+service+"') and transaction.type " +
                " is not null " +
                " and  \\\"@timestamp\\\">=DATETIME_PARSE('"+startTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('"+endTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                " group by error.grouping_key order by error_time desc \",\"time_zone\":\"Asia/Shanghai\"}";
        return errorLogSql;
    }

    /**
     * 根据请求地址获取trace.id和最近一次请求时间
     * 默认判断条件关系符号： =
     * @param urlFull 请求地址
     * @return
     */
    public static String getTransactionSql(String urlFull,String condition,String startTime,String endTime){
        //默认判断条件关系符号： =
        if (StringUtils.isEmpty(condition)){
            condition = EQUAL;
        }

        String transactionSql = "";

        List<String> strings = Arrays.asList(EQUAL, LIKE);
        //不支持的条件类型，默认返回 =
        if (!strings.contains(condition) || StringUtils.equals(condition,EQUAL)){
            transactionSql = "{\"query\": \"select " +
                    "transaction.type,service.environment,"+
                    "host.name,"+
                    " \\\"@timestamp\\\", " +
                    " service.name, " +
                    " transaction.duration.us,"+
                    " timestamp.us, " +
                    " transaction.id, " +
                    " trace.id, " +
                    " url.full, " +
                    " url.domain, " +
                    " url.port, " +
                    " http.request.method, " +
                    " http.response.status_code, " +
                    " http.response.body.content,"+
                    " container.id " +
                    " FROM \\\"apm-*-transaction\\\" " +
                    " where url.full "+EQUAL+" '" + urlFull + "' " +
                    " and  \\\"@timestamp\\\">=DATETIME_PARSE('"+startTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                    " and \\\"@timestamp\\\" <=DATETIME_PARSE('"+endTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                    " order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        }

        if (StringUtils.equalsIgnoreCase(condition,LIKE)){
            transactionSql = "{\"query\": \"select " +
                    "transaction.type,service.environment,"+
                    "host.name,"+
                    "\\\"@timestamp\\\", " +
                    "service.name, " +
                    "transaction.duration.us,"+
                    "timestamp.us, " +
                    "transaction.id, " +
                    "trace.id, " +
                    "url.full, " +
                    "url.domain, " +
                    "url.port, " +
                    "http.request.method, " +
                    "http.response.status_code, " +
                    "http.response.body.content,"+
                    "container.id " +
                    "FROM \\\"apm-*-transaction\\\" " +
                    "where url.full "+condition+" '%" + urlFull + "%' " +
                    " and  \\\"@timestamp\\\">=DATETIME_PARSE('"+startTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                    " and \\\"@timestamp\\\" <=DATETIME_PARSE('"+endTime+"', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                    "order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        }
        return transactionSql;
    }


    /**
     * 获取服务链路信息
     * @param traceId
     * @return
     */
    public static String getSpanSql(String traceId){
        String spanTableSql = "{\"query\": \"select " +
                "host.name,"+
                "\\\"@timestamp\\\", " +
                "service.name, " +
                "transaction.id, " +
                "container.id, " +
                "span.name, " +
                "span.duration.us, " +
                "span.destination.service.resource, " +
                "trace.id, " +
                "span.type, " +
                "span.subtype, " +
                "destination.address, " +
                "destination.ip, " +
                "destination.port, " +
                "event.outcome, " +
                "observer.hostname " +
                "FROM \\\"apm-*-span\\\" " +
                "where trace.id = '" + traceId + "' " +
                "order by \\\"@timestamp\\\" asc\",\"time_zone\":\"Asia/Shanghai\"}";
        return spanTableSql;
    }

    /**
     * error表判断服务是否报错
     */
    public static String getErrorByServiceNameSql(String serviceName){
        String sql = "{\"query\":\"select " +
                "\\\"@timestamp\\\", " +
                "service.name, " +
                "transaction.id, " +
                "transaction.name, " +
//                "error.exception.type, " +
                "trace.id " +
                "FROM \\\"apm-*-error\\\"  " +
                "where LCASE(service.name)=LCASE('%s') " +
                "order by \\\"@timestamp\\\" desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * error表判断服务是否报错
     */
    public static String getErrorSql(String traceId){
        String sql = "{\"query\":\"select " +
                "\\\"@timestamp\\\", " +
                "service.name, " +
                "transaction.id, " +
                "transaction.name, " +
//                "error.exception.type, " +
                "trace.id " +
                "FROM \\\"apm-*-error\\\"  " +
                "where trace.id = '" + traceId + "' " +
                "order by \\\"@timestamp\\\" desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getConditionTimes(String startTime,String endTime){
        String where = "";
        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
            where = String.format(" and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                    " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')",startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
        }
        return where;
    }

    /**
     *
     * @param time 带单位
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getConditionTimes(String time,String startTime,String endTime){
        String where = String.format(" and \\\"@timestamp\\\">=CURRENT_TIMESTAMP() - interval %s ",time);
        if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
            where = String.format(" and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                    " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')",startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR );
        }
        return where;
    }

    /**
     *
     * @param time 不带单位
     * @param unit
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getConditionTimes(String time,String unit,String startTime,String endTime){
        String where = "";
        if(time.equals(TimeEnums.CUSTOM_TIME.getCode()) && StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
            where = String.format(" and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                    " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')",startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR );
        }else {
           double timeLength = TimeEnums.getValue(Integer.parseInt(time));
            where = String.format(" and \\\"@timestamp\\\">=CURRENT_TIMESTAMP() - interval %s %s",timeLength,MINUTE);
        }
        return where;
    }

    /**
     * 详情-概览-服务器信息-hostname
     */
    public static String getTransactionHostnameSql(String serviceName){
        String sql = "{\"query\": \"select host.name " +
                " from \\\"apm-*-transaction\\\"" +
                " where 1=1" +
                " and LCASE(service.name)=LCASE('%s')"+
                " group by host.name\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * 详情-概览-服务器信息-延迟、吞吐量、请求总量
     */
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
    }

    /**
     * 详情-概览-服务器信息-延迟、吞吐量、请求总量
     */
    public static String getTransactionServerNoTimeSql(String time,String serviceName,String startTime,String endTime,String traceId) throws ParseException {
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
                " and LCASE(service.name)=LCASE('%s') "
                +traceIdWhere+
                " group by timestamp1,host.name,container.id,service.name,container.id,service.node.name" +
                " order by timestamp1 desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 详情-概览-服务器信息-所有服务分组统计-请求错误次数
     */
    public static String getTransactionErrorRequestSql(String traceId,String time,String serviceName,String startTime,String endTime){
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select container.id,service.name,service.node.name,count(1) errorRequestCount" +
                " from \\\"apm-*-transaction\\\" where 1=1 " +
                getConditionTimes(time,startTime,endTime)+
                " and LCASE(service.name)=LCASE('%s')" +
                " and event.outcome='failure' "+
                traceIdWhere+
                " group by container.id,service.name," +
                " service.node.name\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * 详情-概览-服务器信息-通过Metricbeat获取IP by container.id
     */
    public static String getMetricbeatIpSql(String containerId){
        String metricbeatSql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "host.ip_str, " +
                "service.name, " +
                "container.id " +
                "from \\\"metricbeat-*\\\" where container.id = '" + containerId + "' " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return metricbeatSql;
    }


    /**
     * 详情-概览-服务器信息-获取容器的查询吞吐量
     * */
    public static String getTransactionTpmSql(String containerId,String serviceName,String serviceNodeName,int time,String startTime,String endTime) throws ParseException {
        String sql = "{\"query\": \"select  " +
                " histogram(\\\"@timestamp\\\",interval " +FullLinkSql.getTimeMinuteLength(startTime,endTime)+" minute) as timestamp1," +
                " count(1)/"+FullLinkSql.getTimeMinuteLength(startTime,endTime)+" as tpm,"+
                " host.name,service.name,container.id,service.node.name " +
                " from \\\"apm-*-transaction\\\"" +
                " where LCASE(service.name)=LCASE('"+serviceName+"') " +
                (StringUtils.isNotEmpty(containerId) ? " and container.id ='"+containerId+"'":"")+
                " and service.node.name ='"+serviceNodeName+"'"+
                getConditionTimes(String.valueOf(time),startTime,endTime)+
                " group by timestamp1,host.name,container.id,service.name," +
                " service.node.name order by timestamp1 desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }


    /**
     * 详情-概览-服务器信息-获取容器的延迟平均值
     */
    public static String getContainerLatencySql(String containerId,String serviceName,String serviceNodeName,int time,String startTime,String endTime) throws ParseException {
        String sql = "{\"query\": \"select  avg(transaction.duration.us) latencyAvg," +
                " histogram(\\\"@timestamp\\\",interval " +FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp1," +
                " host.name,service.name,container.id,service.node.name " +
                " from \\\"apm-*-transaction\\\"" +
                " where LCASE(service.name)=LCASE('"+serviceName+"') " +
                (StringUtils.isNotEmpty(containerId) ? " and container.id ='"+containerId+"'":"")+
                " and service.node.name ='"+serviceNodeName+"'"+
                getConditionTimes(String.valueOf(time),startTime,endTime)+
                " group by timestamp1,host.name,container.id,service.name," +
                " service.node.name order by timestamp1 desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-概览-服务器信息-获取cpu平均值
     */
    public static String getMetricCpuSql(String serviceNodeName,String serviceName,String time,String startTime,String endTime){
        String metricSql = "{\"query\": \"select sum(system.process.cpu.total.norm.pct)/count(1) cpuAvg" +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " and metricset.name='app' " +
                getConditionTimes(time,startTime,endTime)+
                " and system.process.cpu.total.norm.pct is not null" +
                " and service.node.name ='"+serviceNodeName+"'\",\"time_zone\":\"Asia/Shanghai\"}";
        return metricSql;
    }

    /**
     * 详情-概览-服务器信息-获取内存平均值
     */
    public static String getMetricRamSql(String serviceNodeName,String serviceName,String time,String startTime,String endTime){
        String metricSql = "{\"query\": \"select sum(memory_usage)/count(1) ramAvg" +
                " from (select service.node.name,cast(system.process.cgroup.memory.mem.usage.bytes as double)" +
                "/system.memory.total memory_usage from \\\"apm-*-metric\\\"" +
                " where LCASE(service.name)=LCASE('"+serviceName+"') and metricset.name='app'" +
                getConditionTimes(time,startTime,endTime)+
                " and system.memory.total is not null and" +
                " service.node.name='"+serviceNodeName+"')\",\"time_zone\":\"Asia/Shanghai\"}";
        return metricSql;
    }

    /**
     * 详情-概览-延迟-平均值-按月分段分组查询
     */
    public static String getTransactionLatencyAvgGroupByMonthSql(String serviceName,String time,String unit){
        String sql = "{\"query\": \"select month(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " group by month(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-概览-延迟-平均值-按周分段分组查询
     */
    public static String getTransactionLatencyAvgGroupByWeekSql(String serviceName,String time,String unit){
        String sql = "{\"query\": \"select week(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " group by week(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-概览-延迟-平均值-按天分段分组查询
     */
    public static String getTransactionLatencyAvgGroupByDaySql(String serviceName,String time,String unit){
        String sql = "{\"query\": \"select day(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " group by day(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }


    /**
     * 详情-概览-延迟-平均值-按小时分段分组查询
     */
    public static String getTransactionLatencyAvgGroupByHourSql(String serviceName,String time,String unit){
        String sql = "{\"query\": \"select hour(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) )+1 as timeFrame,sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " group by hour(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-概览-延迟-平均值-按分钟分段分组查询
     */
    public static String getTransactionLatencyAvgGroupByMinuteSql(String serviceName,String time,String unit){
        String sql = "{\"query\": \"select minute(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) )+1 as timeFrame,sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('"+serviceName+"') " +
                " group by minute(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-概览-延迟-查询
     */
    public static String getTransactionLatencyAvgSql(String serviceName,String time,String unit,String startTime,String endTime,String traceId) throws ParseException {
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "  {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" minute) as timeFrame," +
                " sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" " +
                " where LCASE(service.name)=LCASE('%s') "
                +traceIdWhere+
                getConditionTimes(time,unit,startTime,endTime)+
                " group by timeFrame \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * 详情-概览-延迟-查询（告警线）
     */
    public static String getTransactionLatencyAvgByHealthStatusSql(String serviceName,String time,String unit,String startTime,String endTime,String traceId,String thresholdValue) throws ParseException {
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "  {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" minute) as timeFrame," +
                " sum(transaction.duration.us)/count(1) as latencyAvg" +
                " from \\\"apm-*-transaction\\\" " +
                " where LCASE(service.name)=LCASE('%s') "
                +traceIdWhere+
                getConditionTimes(time,unit,startTime,endTime)+
                " group by timeFrame" +
                " having latencyAvg >= "+thresholdValue+
                " \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }


    /**
     * 获取所有服务
     */
    public static String getSpanServiceByTraceIdSql(String traceId){
        String sql = " {\"query\": \"SELECT service.id,service.name,service.environment" +
                " FROM \\\"apm-*-transaction\\\"" +
                " where trace.id = '"+traceId+"'" +
                "  GROUP BY service.id,service.name,service.environment\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 获取所有服务
     */
    public static String getSpanServiceSql(){
        String sql = " {\"query\": \"SELECT service.id,service.name,service.environment" +
                " FROM \\\"apm-*-transaction\\\"" +
                "  GROUP BY service.id,service.name,service.environment\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 获取所有服务(刷新服务用)
     */
    public static String getrefreshServiceSql(){
      /*  String sql = " {\"query\": \"SELECT service.name" +
                " FROM \\\"apm-*-metric-*\\\"" +
                "  GROUP BY service.name\",\"time_zone\":\"Asia/Shanghai\"}";*/
        String sql = "{\"query\": \"SELECT service.name FROM \\\"apm-*\\\"" +
                " where service.name is not null  " +
                "and \\\"@timestamp\\\">=CURRENT_TIMESTAMP() - INTERVAL '15' MINUTE " +
                "and \\\"@timestamp\\\"<CURRENT_TIMESTAMP() " +
                " GROUP BY service.name \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 根据服务名称，获取指定时间点内的事务、延迟、吞吐量、请求总量
     */
    public static String getTransactionNameSql(String serviceName,String time,String unit,String startTime,String endTime,String traceId) throws ParseException {
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select service.environment,transaction.type,service.name,transaction.name" +
                " ,sum(transaction.duration.us)/count(1) latencyAvg" +
                " ,count(1)/"+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" as tpm"+
                " ,count(1) as requestTotal" +
                " FROM \\\"apm-*-transaction\\\" where" +
                " LCASE(service.name)=LCASE('%s')"
                +traceIdWhere+
                getConditionTimes(time,unit,startTime,endTime)+
                " group by service.environment,transaction.type,service.name,transaction.name \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,  serviceName);
    }

    /**
     * 指定服务按事务统计-请求错误次数
     */
    public static String getTransactionErrorRequestByServiceSql(String serviceName,String time,String unit,String startTime,String endTime,String traceId){
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select transaction.name, count(1) errorRequestCount" +
                " from \\\"apm-*-transaction\\\" where LCASE(service.name)=LCASE('%s')" +
                getConditionTimes(time,unit,startTime,endTime)
                +traceIdWhere+
                " and event.outcome='failure' " +
                " group by transaction.name\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql, serviceName);
    }

    /**
     * 通过服务和事务名称获取最新的traceId
     */
    public static String getTransactionTraceId(String serviceName,String transactionName){
        String sql = "{\"query\": \"select service.name,trace.id FROM \\\"apm-*-transaction\\\"" +
                " where LCASE(service.name)=LCASE('%s') and transaction.name = '%s'" +
                " order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql, serviceName, transactionName);
    }



    /**
     * 查询具体单个事务-查询事务的花费时间、开始的时间点
     */
    public static String getTransactionDetailSql(String serviceName,String traceId){
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select service.name,trace.id,transaction.id,transaction.name," +
                " transaction.duration.us,timestamp.us from \\\"apm-*-transaction\\\"" +
                " where 1=1 "+traceIdWhere+" and LCASE(service.name)=LCASE('%s')\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * 查询具体单个事务-查询事务的各span花费时间、开始的时间点
     */
    public static String getTransactionDetailSpanSql(String serviceName,String traceId){
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select service.name,trace.id,transaction.id,parent.id," +
                " span.id,span.name,span.duration.us,timestamp.us from \\\"apm-*-span\\\"" +
                " where  transaction.name!='ZuulServlet#doPost' "+traceIdWhere+" and" +
                " LCASE(service.name)=LCASE('%s') order by \\\"@timestamp\\\"\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

    /**
     * 查询指定服务的错误日志统计
     */
    public static String getErrorLogsCountSql(String serviceName,String timeNoUnit,String unit,String startTime,String endTime,String traceId,int time,String containerId) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String containerIdWhere = "";
        if (StringUtils.isNotEmpty(containerId)){
            containerIdWhere = " and container.id = '"+containerId+"' ";
        }
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame," +
                "count(1) errorNumber from \\\"apm-*-error\\\" " +
                "where LCASE(service.name)=LCASE('%s')" +
                where
                +traceIdWhere+
                containerIdWhere+
                "  group by timeFrame\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql, serviceName,time,unit);
    }

    /**
     * 查询指定服务的错误日志
     */
    public static String getErrorLogsSql(String serviceName,int time,String timeNoUnit,String unit,String startTime,String endTime,String traceId) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame," +
                "error.grouping_key as errorGroupingKey,count(1) errorNumber," +
                " last(\\\"@timestamp\\\",timestamp.us) timestamp," +
                " last(error.grouping_name,timestamp.us) name,last(error.culprit,timestamp.us) errorCulprit" +
                " from \\\"apm-*-error\\\" where LCASE(service.name)=LCASE('%s')" +
                where
                +traceIdWhere+
                 " and error.grouping_key is not null "+
                " group by timeFrame,error.grouping_key" +
                " having errorNumber>0 "+
                " order by timeFrame,errorNumber desc  \",\"time_zone\":\"Asia/Shanghai\"}";
//                " group by timeFrame,error.grouping_key order by timeFrame,errorNumber desc  limit 50 \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql, serviceName,time,unit);
    }


    /**
     * 查询指定服务的错误日志-按月分段分组查询
     */
    public static String getErrorLogGroupByMonthSql(String errorGroupingKey,String time,String unit){
        String sql = "{\"query\": \"select month_of_year(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,count(1) as errorNumber" +
                " from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                " group by month_of_year(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询指定服务的错误日志-按周分段分组查询
     */
    public static String getErrorLogGroupByWeekSql(String errorGroupingKey,String time,String unit){
        String sql = "{\"query\": \"select day_of_month(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,count(1) as errorNumber" +
                " from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                " group by day_of_month(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询指定服务的错误日志-按天分段分组查询
     */
    public static String getErrorLogGroupByDaySql(String errorGroupingKey,String time,String unit){
        String sql = "{\"query\": \"select day_of_week(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,count(1) as errorNumber" +
                " from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                " group by day_of_week(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }


    /**
     * 查询指定服务的错误日志-按小时分段分组查询
     */
    public static String getErrorLogGroupByHourSql(String errorGroupingKey,String time,String unit){
        String sql = "{\"query\": \"select hour_of_day(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,count(1) as errorNumber" +
                " from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                " group by hour_of_day(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询指定服务的错误日志-按分钟分段分组查询
     */
    public static String getErrorLogGroupByMinuteSql(String errorGroupingKey,String time,String unit){
        String sql = "{\"query\": \"select minute_of_hour(DATE_ADD ( '"+unit+"' , -"+time+" ," +
                " \\\"@timestamp\\\" :: datetime ) ) as timeFrame,count(1) as errorNumber" +
                " from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                " group by minute_of_hour(DATE_ADD ('"+unit+"' , -"+time+", \\\"@timestamp\\\" :: datetime ) ) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询指定服务的错误日志-按分钟分段分组查询
     */
    public static String getErrorLogSql(String errorGroupingKey,String time,String unit,String startTime,String endTime) throws ParseException {
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" minute) as timeFrame,count(1) as errorNumber " +
                "from \\\"apm-*-error\\\" where error.grouping_key='"+errorGroupingKey+"'" +
                getConditionTimes(time,unit,startTime,endTime)+
                " group by timeFrame \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 服务依赖(依赖项、吞吐量、延迟、请求总数(错误请求数))
     */
    public static String getSpanServiceDependenciesSql(String serviceName,String time,String unit,Boolean failureRequest,String startTime,String endTime,String traceId) throws ParseException {
        String traceIdWhere = "";
        if (StringUtils.isNotEmpty(traceId)){
            traceIdWhere = " and trace.id = '"+traceId+"' ";
        }
        String failureRequestString = failureRequest ? " and event.outcome='failure'" : "" ;
        String sql = " {\"query\": \"select span.destination.service.resource," +
                " count(1)/"+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" as tpm,"+
//        String sql = " {\"query\": \"select span.destination.service.resource,count(1)/%s/"+SIXTYMINUTES+" as tpm," +
                " sum(span.duration.us)/count(1) as latencyAvg,count(1) requestTotal" +
                " from \\\"apm-*-span\\\"  where span.destination.service.resource is not null " +
                "and LCASE(service.name)=LCASE('%s')" +
                getConditionTimes(time,unit,startTime,endTime)+
                failureRequestString
                +traceIdWhere+
                " group by span.destination.service.resource\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,time,unit);
    }

    /**
     * 详情-服务-获取jvm的CPU使用率
     */
    public static String getJvmCpuSql(String serviceName,String startTime,String endTime,int time,String containerId) throws ParseException {
        String sql = " {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " max(system.cpu.total.norm.pct) system_max, " +
                " avg(system.cpu.total.norm.pct) system_avg," +
//                " sum(system.cpu.total.norm.pct)/count(1) system_avg," +
                " max(system.process.cpu.total.norm.pct) process_max," +
                " avg(system.process.cpu.total.norm.pct) process_avg " +
//                " sum(system.process.cpu.total.norm.pct)/count(1) process_avg " +
                " from \\\"apm-*-metric\\\" " +
                " where LCASE(service.name)=LCASE('%s') " +
                " and metricset.name='app'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '"+SIMPLE_DATE_FORMAT_STR+"')"+
                " and system.cpu.total.norm.pct is not null " +
                " and container.id='%s'"+
                " group by timestamp " +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,endTime,containerId);
    }

    /**
     *
     */
    public static String getJvmCpuByHealthStatus(String serviceName,String startTime,String endTime,int time,String containerId,String thresholdValue) throws ParseException {
        String sql = " {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " max(system.cpu.total.norm.pct) system_max, " +
                " avg(system.cpu.total.norm.pct) system_avg," +
//                " sum(system.cpu.total.norm.pct)/count(1) system_avg," +
                " max(system.process.cpu.total.norm.pct) process_max," +
                " avg(system.process.cpu.total.norm.pct) process_avg " +
//                " sum(system.process.cpu.total.norm.pct)/count(1) process_avg " +
                " from \\\"apm-*-metric\\\" " +
                " where LCASE(service.name)=LCASE('%s') " +
                " and metricset.name='app'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '"+SIMPLE_DATE_FORMAT_STR+"')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '"+SIMPLE_DATE_FORMAT_STR+"')"+
                " and system.cpu.total.norm.pct is not null " +
                " and container.id='%s'"+
                " group by timestamp " +
                " having system_avg>=%s"+
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,endTime,containerId,thresholdValue);
    }



    /**
     * 详情-服务-获取内存使用率
     */
    public static String getJvmRamSql(String serviceName,String startTime,String endTime,int time,String containerId) throws ParseException {
        String sql = "   {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp,sum(memory_usage)/count(1) avg_usage_pct," +
                "max(memory_usage) max_usage_pct from (select \\\"@timestamp\\\"," +
                "cast(system.process.cgroup.memory.mem.usage.bytes as double)/system.memory.total memory_usage" +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s') and metricset.name='app'" +
                " and system.memory.total is not null" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " and container.id='%s'"+
                ")" +
                " group by timestamp  order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId);
    }

    /**
     * 详情-服务-获取内存使用率(告警线)
     */
    public static String getJvmHeapRamByHealthStatus(String serviceName,String startTime,String endTime,int time,String containerId,String thresholdValue) throws ParseException {
        String sql = "   {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp,sum(memory_usage)/count(1) avg_usage_pct," +
                "max(memory_usage) max_usage_pct from (select \\\"@timestamp\\\"," +
                "cast(system.process.cgroup.memory.mem.usage.bytes as double)/system.memory.total memory_usage" +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s') and metricset.name='app'" +
                " and system.memory.total is not null" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " and container.id='%s')"+
                " group by timestamp" +
                " having avg_usage_pct>=%s"+
                "  order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId,thresholdValue);
    }


    /**
     * 详情-服务-获取堆内存使用率
     */
    public static String getJvmHeapRamSql(String serviceName,String startTime,String endTime,int time,String containerId) throws ParseException {
        String sql = "  {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp, " +
                " sum(jvm.memory.heap.used)/count(1) as memory_heap_used," +
//                " sum(jvm.memory.heap.used)/count(1)/1024.0/1024/1024 as memory_heap_used," +
                " sum(jvm.memory.heap.committed)/count(1) as memory_heap_committed," +
//                " sum(jvm.memory.heap.committed)/count(1)/1024.0/1024/1024 as memory_heap_committed," +
                " sum(jvm.memory.heap.max)/count(1) as memory_heap_max " +
//                " sum(jvm.memory.heap.max)/count(1)/1024.0/1024/1024 as memory_heap_max " +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s') " +
                " and metricset.name='app' and" +
                " \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s') " +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " and jvm.memory.heap.used is not null" +
                " and container.id='%s'"+
                " group by timestamp  " +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId);
    }

    /**
     * 详情-服务-获取堆内存使用率(告警线)
     */
    public static String getJvmHeapRamByHealthStatusSql(String serviceName,String startTime,String endTime,int time,String containerId,String thresholdValue) throws ParseException {
        String sql = "  {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp, " +
                " sum(jvm.memory.heap.used)/count(1) as memory_heap_used," +
//                " sum(jvm.memory.heap.used)/count(1)/1024.0/1024/1024 as memory_heap_used," +
                " sum(jvm.memory.heap.committed)/count(1) as memory_heap_committed," +
//                " sum(jvm.memory.heap.committed)/count(1)/1024.0/1024/1024 as memory_heap_committed," +
                " sum(jvm.memory.heap.max)/count(1) as memory_heap_max " +
//                " sum(jvm.memory.heap.max)/count(1)/1024.0/1024/1024 as memory_heap_max " +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s') " +
                " and metricset.name='app' and" +
                " \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s') " +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " and jvm.memory.heap.used is not null" +
                " and container.id='%s'"+
                " group by timestamp  " +
                " having memory_heap_used>=%s"+
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId,thresholdValue);
    }



    /**
     * 详情-服务-获取非堆内存使用率
     */
    public static String getJvmNonHeapRamSql(String serviceName,String startTime,String endTime,int time,String containerId) throws ParseException {
        String sql = " {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " sum(jvm.memory.non_heap.used)/count(1) as memory_non_heap_used," +
//                " sum(jvm.memory.non_heap.used)/count(1)/1024.0/1024 as memory_non_heap_used," +
                " sum(jvm.memory.non_heap.committed)/count(1) as memory_non_heap_committed" +
//                " sum(jvm.memory.non_heap.committed)/count(1)/1024.0/1024 as memory_non_heap_committed" +
                " from \\\"apm-*-metric\\\"  where LCASE(service.name)=LCASE('%s')" +
                " and metricset.name='app'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s') " +
                " and jvm.memory.non_heap.used is not null" +
                " and container.id='%s'"+
                " group by timestamp  order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId);
    }

    /**
     * 详情-服务-获取线程计数
     */
    public static String getJvmThreadCountSql(String serviceName,String startTime,String endTime,int time,String containerId) throws ParseException {
        String sql = "  {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " max(jvm.thread.count) max_count," +
                " avg(jvm.thread.count) avg_count "+
//                " sum(cast(jvm.thread.count as double))/count(1)/1000/1000 avg_count " +
                " from \\\"apm-*-metric\\\" " +
                " where LCASE(service.name)=LCASE('%s') " +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s') " +
                " and jvm.thread.count is not null " +
                " and container.id='%s'"+
                " group by timestamp " +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId);
    }

    /**
     * 详情-服务-获取线程计数
     */
    public static String getJvmThreadCountByHealthStatusSql(String serviceName,String startTime,String endTime,int time,String containerId,String thresholdValue) throws ParseException {
        String sql = "  {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " max(jvm.thread.count) max_count," +
                " avg(jvm.thread.count) avg_count "+
//                " sum(cast(jvm.thread.count as double))/count(1)/1000/1000 avg_count " +
                " from \\\"apm-*-metric\\\" " +
                " where LCASE(service.name)=LCASE('%s') " +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s') " +
                " and jvm.thread.count is not null " +
                " and container.id='%s'"+
                " group by timestamp " +
                " having avg_count>= "+thresholdValue+
                " order by timestamp asc " +
                "\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,containerId);
    }


    /**
     * 详情-服务-每分钟垃圾回收(PS MarkSweep|PS Scanvenge)
     */
    public static String getJvmGcSql(String serviceName,String startTime,String endTime,String gcType,int time,String containerId) throws ParseException {
        String sql = "   {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " labels.name,jvm.gc.count" +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s')" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s') " +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')" +
                " and labels.name='%s' " +
                " and container.id='%s'"+
                "group by timestamp ,labels.name,jvm.gc.count" +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,gcType,containerId);
    }

    /**
     * 详情-服务-每分钟垃圾回收(PS MarkSweep|PS Scanvenge)
     */
    public static String getJvmSpentGcTimeSql(String serviceName,String startTime,String endTime,String gcType,int time,String containerId) throws ParseException {
        String sql = "   {\"query\": \" select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " labels.name,jvm.gc.time,jvm.gc.count,cast(jvm.gc.time as double)/jvm.gc.count as jvm_gc_time " +
                " from \\\"apm-*-metric\\\" where LCASE(service.name)=LCASE('%s')" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s') " +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')" +
                " and labels.name='%s'" +
//                " and container.id='%s'"+
                " group by timestamp ,labels.name,jvm.gc.time,jvm.gc.count" +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR,gcType,containerId);
    }

    /**
     * 详情-服务-跨度类型花费的时间（按照span.subtype分组）
     */
    public static String getSpanTypeCostTimeSql(String serviceName,String startTime,String endTime,int time) throws ParseException {
        String sql = " {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " span.subtype,sum(span.self_time.sum.us) as cost_time " +
                " from \\\"apm-*-metric\\\" where metricset.name='span_breakdown'" +
                " and LCASE(service.name)=LCASE('%s') " +
                " and  \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')" +
                " group by timestamp,span.subtype\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 详情-服务-跨度类型花费的时间（按照时间分组求和）
     */
    public static String getSpanCostTimeSql(String serviceName,String startTime,String endTime,int time) throws ParseException {
        String sql = " {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " sum(span.self_time.sum.us) as cost_time " +
                " from \\\"apm-*-metric\\\" where metricset.name='span_breakdown'" +
                " and LCASE(service.name)=LCASE('%s') " +
                " and  \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')" +
                " group by timestamp\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 详情-服务-跨度类型花费的时间比例
     */
    public static String getSpanTypeCostTimeRateSql(String serviceName,String startTime,String endTime,int time){
        String sql = " {\"query\":\"select span.subtype,sum(span.self_time.sum.us) as cost_time " +
                " from \\\"apm-*-metric\\\" where metricset.name='span_breakdown'" +
                " and LCASE(service.name)=LCASE('%s') " +
                " and  \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\" <=DATETIME_PARSE('%s', '%s')" +
                " group by span.subtype\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 详情-数据库-获取数据库基本信息(实例、版本、状态)
     */
    public static String getDatabaseBasicInfo(int databaseType,String startTime,String endTime,String serviceAddress){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "";
        switch (databaseType){
            case 1:
                sql = " {\"query\": \"select \\\"@timestamp\\\",service.address,sql.driver," +
                        " sql.metrics.string.instance_name,sql.metrics.string.oracle_version," +
                        " sql.metrics.string.database_status,sql.metrics.string.status" +
                        " FROM \\\"metricbeat-*\\\" where event.module = 'sql' " +serviceAddressSql+
                        " and sql.metrics.string.oracle_metric_type='version'" +
                        getConditionTimes(startTime,endTime)+
                        " order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
                break;
            case 2:
                break;
            default:
                break;
        }
        return sql;
    }


    /**
     * 详情-数据库-获数据库当前链接数
     */
    public static String getDatabaseCurrentConnectCount(String databaseType,String startTime,String endTime,String serviceAddress,int time){
       //如果时间类型是最近15分钟的话，取最新一条记录即可（添加了时间的话，统计值有误差）
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "";
        switch (databaseType.toLowerCase(Locale.ROOT)){
            case "oracle":
            case "1":
                sql = " {\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.user_name, " +
                        " service.address,sql.driver," +
                        " sql.metrics.numeric.connect_count FROM \\\"metricbeat-*\\\" " +
                        " where event.module = 'sql' and sql.metrics.string.oracle_metric_type ='connect' " +serviceAddressSql+
                        (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                        " order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
                break;
            case "mysql":
            case "2":
                sql = "   {\"query\": \"select service.address,mysql.status.threads.connected " +
                        " FROM \\\"metricbeat-*\\\"" +
                        "  where event.module='mysql' and event.dataset='mysql.status'"  +serviceAddressSql+
                        (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                        " order by \\\"@timestamp\\\" desc limit 1\" ,\"time_zone\":\"Asia/Shanghai\"} ";
                break;
            default:
                break;
        }
        return sql;
    }

    /**
     * 详情-数据库-获数据库允许最大链接数
     */
    public static String getDatabaseMaxConnectCount(String databaseType,String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "";
        switch (databaseType.toLowerCase(Locale.ROOT)){
            case "oracle":
            case "1":
                sql = " {\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.user_name," +
                        " service.address,sql.driver,sql.metrics.numeric.connect_max" +
                        " FROM \\\"metricbeat-*\\\" where event.module = 'sql' " +
                        " and sql.metrics.string.oracle_metric_type='connect_max' "  +serviceAddressSql+
                        (time != TimeEnums.FIFTEEN_MINUTE.getCode()?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                        " order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
                break;
            case "mysql":
            case "2":
                sql = " {\"query\": \"select \\\"@timestamp\\\"," +
                        " mysql.query.connects.max_connections," +
                        " mysql.query.lock.lock_num FROM \\\"metricbeat-*\\\" " +
                        " where event.dataset = 'mysql.query' " +serviceAddressSql+
                        (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                        " order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
                break;
            default:
                break;
        }
        return sql;
    }

    /**
     * 详情-数据库-获数据库表空间（1：用户表空间，2：临时表空间）
     */
    public static String getDatabaseTablespace(String tablespaceType,String startTime,String endTime,String serviceAddress,int time){
        if (StringUtils.isNotEmpty(serviceAddress)){
            String sql = "{\"query\": \"select sql.metrics.string.username,service.address," +
                    " sql.metrics.string.tablespace_name,sql.metrics.string.autoextensible," +
                    " sql.metrics.numeric.usage_amount,sql.metrics.numeric.amounts," +
                    " sql.metrics.numeric.usage_rate FROM \\\"metricbeat-*\\\"" +
                    " where event.module='sql' and service.address = '%s' and sql.metrics.string.oracle_metric_type='%s'" +
                    (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                    " order by \\\"@timestamp\\\" \",\"time_zone\":\"Asia/Shanghai\"}";
            return String.format(sql,serviceAddress,tablespaceType);
        }else {
            String sql = "{\"query\": \"select sql.metrics.string.username,service.address," +
                    " sql.metrics.string.tablespace_name,sql.metrics.string.autoextensible," +
                    " sql.metrics.numeric.usage_amount,sql.metrics.numeric.amounts," +
                    " sql.metrics.numeric.usage_rate FROM \\\"metricbeat-*\\\"" +
                    " where event.module='sql' and sql.metrics.string.oracle_metric_type='%s'" +
                    (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                    " order by \\\"@timestamp\\\" \",\"time_zone\":\"Asia/Shanghai\"}";
            return String.format(sql,tablespaceType);
        }

    }

    /**
     * 详情-数据库-获取数据库内存结构PGA和SGA的使用量
     */
    public static String getDatabasePgaAndSgaRate(String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select \\\"@timestamp\\\",sql.metrics.string.oracle_metric_type," +
                " service.address,sql.metrics.numeric.total,sql.metrics.numeric.used," +
                " sql.metrics.numeric.pctused FROM \\\"metricbeat-*\\\" " +
                " where event.module='sql' and sql.metrics.string.oracle_metric_type IN ('sga','pga')" +serviceAddressSql+
                (time != TimeEnums.FIFTEEN_MINUTE.getCode()?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ") +
                " order by \\\"@timestamp\\\" desc limit 2 \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取数据库内存结构PGA和SGA的使用量
     */
    public static String getDatabaseSgaTop10(String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select \\\"@timestamp\\\",service.address,sql.metrics.string.component," +
                " sql.metrics.numeric.current_size FROM \\\"metricbeat-*\\\" " +
                " where event.module='sql' and sql.metrics.string.oracle_metric_type='sga_detail' " +serviceAddressSql+
                (time != TimeEnums.FIFTEEN_MINUTE.getCode()?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ") +
                " order by \\\"@timestamp\\\",sql.metrics.numeric.current_size desc limit 10 \",\"time_zone\":\"Asia/Shanghai\"}";

        return sql;
    }

    /**
     * 详情-数据库-分页获取数据库内存结构PGA使用列表
     */
    public static String getDatabasePgas(String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "  {\"query\": \"select service.address,sql.metrics.string.username," +
//        String sql = "  {\"query\": \"select \\\"@timestamp\\\" as timestamp,service.address,sql.metrics.string.username," +
                " sql.metrics.string.server,sql.metrics.string.osuser,sql.metrics.numeric.ram_usage_mem," +
                " sql.metrics.numeric.sid,\\\"sql.metrics.numeric.serial#\\\"," +
                " sql.metrics.numeric.pga_used_mem,sql.metrics.numeric.pga_alloc_mem," +
                " sql.metrics.numeric.pga_max_mem FROM \\\"metricbeat-*\\\" " +
                " where event.module='sql' and sql.metrics.string.oracle_metric_type='pga_detail' " + serviceAddressSql+
                (time != TimeEnums.FIFTEEN_MINUTE.getCode()?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ") +
                " \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取数据库死锁列表
     */
    public static String getDatabaseDeadLocks(String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "    {\"query\": \" select " +
                " service.address,sql.driver,sql.driver,sql.metrics.numeric.sid," +
                " sql.metrics.numeric.serial, sql.metrics.string.logon_time," +
                " sql.metrics.string.username,sql.metrics.string.schemaname," +
                " sql.metrics.string.status,sql.metrics.string.lockwait," +
                " sql.metrics.string.machine,sql.metrics.string.osuser," +
                " sql.metrics.string.program,sql.metrics.string.row_wait_obj" +
                " FROM \\\"metricbeat-*\\\"" +
                " where event.module='sql' " +
                " and sql.metrics.string.oracle_metric_type='lock' " +serviceAddressSql+
                (time != TimeEnums.FIFTEEN_MINUTE.getCode()&&StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-分页获取数据库慢SQL列表
     */
    public static String getDatabaseSlowSqls(String startTime,String endTime,String serviceAddress,int time){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "  {\"query\": \"select service.address,sql.metrics.string.sql_text," +
                " sql.metrics.numeric.count,sql.metrics.numeric.total_time," +
                " sql.metrics.numeric.avg_time,sql.metrics.string.first_load_time," +
                " sql.metrics.string.last_load_time,sql.metrics.string.last_active_time," +
                " sql.metrics.string.user_name FROM \\\"metricbeat-*\\\"" +
                " where event.module='sql' and sql.metrics.string.oracle_metric_type='slow' " +
                serviceAddressSql+
                (time != TimeEnums.FIFTEEN_MINUTE.getCode()?getConditionTimes(startTime,endTime):" and \\\"@timestamp\\\" > CURRENT_TIMESTAMP() - INTERVAL '1' MINUTE ")+
                " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取缓存缓冲区命中率指标
     */
    public static String getCacheHitRate(String startTime,String endTime,String serviceAddress){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                " avg(oracle.performance.cache.buffer.hit.pct) cache_buffer_hit_ratio" +
                " FROM \\\"metricbeat-*\\\" where event.module='oracle' " +
                " and event.dataset='oracle.performance' " +serviceAddressSql+
                getConditionTimes(startTime,endTime)+
                " group by service.address \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取锁定/PIN请求和IO重新加载比率
     */
    public static String getLockPinRequestsAndIOReloadsRate(String startTime,String endTime,String serviceAddress){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "  {\"query\": \"select service.address,avg(oracle.performance.io_reloads) io_reloads," +
                "avg(oracle.performance.lock_requests) lock_requests,avg(oracle.performance.pin_requests) pin_requests " +
                "FROM \\\"metricbeat-*\\\" where event.module='oracle'" +serviceAddressSql+
                " and event.dataset='oracle.performance'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取mysql每60秒SELECT语句的查询量
     */
    public static String getMysqlSelectQueries(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = " {\"query\": \"select service.address," +
                " histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key, " +
                "max(\\\"mysql.status.command.select\\\") select_count" +
                " FROM  \\\"metricbeat-*\\\" where event.module='mysql'" +serviceAddressSql+
                " and event.dataset='mysql.status' " +
                getConditionTimes(startTime,endTime)+
                "group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取mysql每60秒DELETE INSERT UPDATE执行量（最大值）
     */
    public static String getMysqlDeleteInsertUpdateCount(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = " {\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(\\\"mysql.status.command.insert\\\") insert_count," +
                "max(\\\"mysql.status.command.update\\\") update_count," +
                "max(\\\"mysql.status.command.delete\\\") delete_count" +
                " FROM \\\"metricbeat-*\\\" where event.module='mysql'" +serviceAddressSql+
                " and event.dataset='mysql.status' " +
                getConditionTimes(startTime,endTime)+
                "group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取mysql终止的连接数（最大值）
     */
    public static String getMysqlAbortedConnectionsMax(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "   {\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(mysql.status.aborted.clients) abort_connection, " +
                "max(mysql.status.aborted.connects) failed_attempt_connect" +
                " FROM \\\"metricbeat-*\\\" where event.module='mysql'" +serviceAddressSql+
                " and event.dataset='mysql.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取mysql线程活动数量（最大值）
     */
    public static String getMysqlThreadActivitiesMax(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "  {\"query\": \"select service.address," +
                " histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                " avg(mysql.status.threads.running) avg_threads_run," +
                " max(mysql.status.threads.running) peak_threads_run," +
                " max(mysql.status.threads.connected) peak_threads_connect" +
                " FROM \\\"metricbeat-*\\\" where event.module='mysql'" +serviceAddressSql+
                " and event.dataset='mysql.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取缓冲池数据页信息
     */
    public static String getMysqlBufferPoolPagesAvg(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                " histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                " avg(mysql.status.innodb.buffer_pool.pages.data) pool_page_data," +
                " avg(mysql.status.innodb.buffer_pool.pages.free) pool_page_free," +
                " avg(mysql.status.innodb.buffer_pool.pages.total) pool_page_total" +
                " FROM  \\\"metricbeat-*\\\" where event.module = 'mysql'" +serviceAddressSql+
                " and event.dataset='mysql.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取缓冲池利用率
     */
    public static String getMysqlBufferPoolUtilization(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address,bucket_key," +
                "1-cast(pool_page_free as double)/ pool_page_total utilization" +
                " from(select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(mysql.status.innodb.buffer_pool.pages.free) pool_page_free," +
                "max(mysql.status.innodb.buffer_pool.pages.total) pool_page_total" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'mysql'" +serviceAddressSql+
                " and event.dataset = 'mysql.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key) \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-数据库-获取缓冲池效率
     */
    public static String getMysqlBufferPoolEfficiency(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "bucket_key,cast(pool_reads as double)/pool_read_requests efficiency" +
                " from(select service.address,histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(mysql.status.innodb.buffer_pool.pool.reads) pool_reads," +
                "max(mysql.status.innodb.buffer_pool.read.requests) pool_read_requests" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'mysql'" +serviceAddressSql+
                " and event.dataset = 'mysql.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key)\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取redis密钥空间选择器
     */
    public static String getRedisKeyspaceSelector(String startTime,String endTime,String serviceAddress){
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address,first(redis.keyspace.id) keyspace" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'redis'" +
                " and event.dataset = 'redis.keyspace' " +serviceAddressSql+
                getConditionTimes(startTime,endTime)+
                "group by service.address\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取redis按类型划分的键
     */
    public static String getRedisKeysByType(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select first(redis.keyspace.id) keyspace," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "service.address,redis.key.type,count(distinct redis.key.id) as count" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'redis'" +serviceAddressSql+
                " and event.dataset = 'redis.key'" +
                getConditionTimes(startTime,endTime)+
                " group by bucket_key,service.address,redis.key.type \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取redis字符串键的平均大小
     */
    public static String getRedisAverageStringKeySize(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select first(redis.keyspace.id) keyspace," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "service.address, redis.keyspace.id,avg(redis.key.length) string_length" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'redis'" +serviceAddressSql+
                " and redis.key.type='string' " +
                getConditionTimes(startTime,endTime)+
                "group by bucket_key,service.address,redis.keyspace.id \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取redis平均键TTL
     */
    public static String getRedisAverageKeysTtl(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select first(redis.keyspace.id) keyspace," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "service.address, redis.keyspace.id,redis.key.type,avg(redis.key.expire.ttl) ttl_avg" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'redis'" +serviceAddressSql+
                " and redis.key.type='string' " +
                getConditionTimes(startTime,endTime)+
                "group by bucket_key,service.address,redis.keyspace.id,redis.key.type \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取rabbitmq Erlang进程使用情况
     */
    public static String getRabbitmqErlangProcessUsage(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(rabbitmq.node.proc.used) as proc_used" +
                " FROM \\\"metricbeat-*\\\"" +
                " where event.module = 'rabbitmq' " +serviceAddressSql+
                "and event.dataset = 'rabbitmq.node'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取Rabbitmq 队列索引操作
     */
    public static String getRabbitmqQueueIndexOperations(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(rabbitmq.node.queue.index.read.count) as read_count," +
                "max(rabbitmq.node.queue.index.journal_write.count) as journal_write_count," +
                "max(rabbitmq.node.queue.index.write.count) as write_count " +
                "FROM \\\"metricbeat-*\\\" where event.module = 'rabbitmq' " +serviceAddressSql+
                "and event.dataset = 'rabbitmq.node' " +
                getConditionTimes(startTime,endTime)+
                "group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取mongodb 操作计数器
     */
    public static String getMongodbOperationCounters(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "avg(mongodb.status.ops.counters.command) command," +
                "avg(mongodb.status.ops.counters.insert) insert," +
                "avg(mongodb.status.ops.counters.query) query," +
                "avg(mongodb.status.ops.counters.update) update," +
                "avg(mongodb.status.ops.counters.delete) delete," +
                "avg(mongodb.status.ops.counters.getmore) getmore " +
                "FROM \\\"metricbeat-*\\\" where event.module = 'mongodb'" +serviceAddressSql+
                " and event.dataset = 'mongodb.status' " +
                getConditionTimes(startTime,endTime)+
                "group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取mongodb 并发事务读取
     */
    public static String getMongodbConcurrentTransactionsRead(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "avg(mongodb.status.wired_tiger.concurrent_transactions.read.out) read_used," +
                "avg(mongodb.status.wired_tiger.concurrent_transactions.read.available) read_available" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'mongodb'" +serviceAddressSql+
                " and event.dataset = 'mongodb.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取mongodb 并发事务写入
     */
    public static String getMongodbConcurrentTransactionsWrite(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "avg(mongodb.status.wired_tiger.concurrent_transactions.write.out) write_used," +
                "avg(mongodb.status.wired_tiger.concurrent_transactions.write.available) write_available" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'mongodb'" +serviceAddressSql+
                " and event.dataset = 'mongodb.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取mongodb WiredTiger缓存
     */
    public static String getMongodbWiredTigerCache(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "avg(mongodb.status.wired_tiger.cache.maximum.bytes) cache_max," +
                "avg(mongodb.status.wired_tiger.cache.used.bytes) cache_used," +
                "avg(mongodb.status.wired_tiger.cache.dirty.bytes) cache_dirty " +
                "FROM \\\"metricbeat-*\\\" where event.module = 'mongodb'" +serviceAddressSql+
                " and event.dataset = 'mongodb.status' " +
                getConditionTimes(startTime,endTime)+
                "group by service.address,bucket_key \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 详情-中间件-获取mongodb Asserts断言
     */
    public static String getMongodbAsserts(String startTime,String endTime,String serviceAddress,int time) throws ParseException {
        String serviceAddressSql = "";
        if (StringUtils.isNotEmpty(serviceAddress)){
            serviceAddressSql =" and service.address like '%"+serviceAddress+"%' ";
        }
        String sql = "{\"query\": \"select service.address," +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) bucket_key," +
                "max(mongodb.status.asserts.regular) regular," +
                "max(mongodb.status.asserts.warning) warning," +
                "max(mongodb.status.asserts.msg) message," +
                "max(mongodb.status.asserts.user) user," +
                "max(mongodb.status.asserts.rollovers) rollovers" +
                " FROM \\\"metricbeat-*\\\" where event.module = 'mongodb'" +serviceAddressSql+
                " and event.dataset = 'mongodb.status'" +
                getConditionTimes(startTime,endTime)+
                " group by service.address,bucket_key\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询系统盘使用信息平均值
     */
    public static String getSystemDiskAvg(String ip, Map<String, String> times,int time,String startTime,String endTime) throws ParseException {
        String sql ="{\"query\":\"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                "host.name, avg(system.fsstat.total_size.used), avg(system.fsstat.total_size.total) " +
                "from \\\"metricbeat-*\\\" " +
                "where event.dataset = 'system.fsstat' " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "group by timestamp,host.name " +
                "order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询系统盘使用信息
     */
    public static String getSystemDisk(String ip, Map<String, String> times){
        String sql = "{\"query\":\"select " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.fsstat.total_size.used, " +
                "system.fsstat.total_size.total " +
                "from \\\"metricbeat-*\\\" where " +
                "event.dataset = 'system.fsstat' " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询cpu信息平均值
     */
    public static String getCpuMetricbeatAvg(String ip, Map<String, String> times,int time,String startTime,String endTime) throws ParseException {
        String sql ="{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
//                " host.name, system.cpu.total.norm.pct, system.cpu.user.pct, system.cpu.cores" +
                " host.name, avg(system.cpu.total.norm.pct), avg(system.cpu.total.pct), avg(system.cpu.cores)" +
                " from \\\"metricbeat-*\\\" where  event.dataset = 'system.cpu'" +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                " group by timestamp, host.name " +
                " order by timestamp asc \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询cpu信息
     */
    public static String getCpuMetricbeat(String ip, Map<String, String> times){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.cpu.total.norm.pct, " +
                "system.cpu.total.pct, " +
                "system.cpu.cores " +
                "from \\\"metricbeat-*\\\" where  " +
                "event.dataset = 'system.cpu'  " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询内存信息平均值
     */
    public static String getRamMetricbeatAvg(String ip, Map<String, String> times,int time,String startTime,String endTime) throws ParseException {
        String sql ="{\"query\":\"select  histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp," +
                " host.name, avg(system.memory.actual.used.bytes), avg(system.memory.total)" +
                " from \\\"metricbeat-*\\\" where  event.dataset = 'system.memory'  " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "group by timestamp,host.name order by timestamp asc\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询内存信息
     */
    public static String getRamMetricbeat(String ip, Map<String, String> times){
        String sql = "{\"query\":\"select  " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.memory.actual.used.bytes, " +
                "system.memory.total " +
                "from \\\"metricbeat-*\\\" where  " +
                "event.dataset = 'system.memory'  " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询网络信息
     */
    public static String getNetworksMetricbeat(String ip, Map<String, String> times){
        String sql =  "{\"query\":\"select  " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "\\\"system.network.in.bytes\\\", " +
                "system.network.out.bytes, " +
                "system.network.name " +
                "from \\\"metricbeat-*\\\" where  " +
                "system.network.name is not null and " +
                "event.dataset = 'system.network'  " +
                "and host.ip_str like '%" + ip + "%' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "order by \\\"@timestamp\\\" desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 获取事务信息
     */
    public static String getTransaction(String serviceName){
        String sql =   "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.name,"+
                "service.name, " +
                "timestamp.us," +
                "trace.id, " +
                "url.full, " +
                "url.domain, " +
                "url.port, " +
                "http.request.method, " +
                "container.id " +
                "FROM \\\"apm-*-transaction\\\" " +
                "where LCASE(service.name)=LCASE('%s') " +
                "order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,serviceName);
    }

//    /**
//     * 公共方法_根据serviceName获取服务ContainerId下拉框sql
//     */
//    public static String getServiceContainerId(String serviceName){
//        String sql =   "{\"query\": \"select " +
//                "container.id " +
//                "FROM \\\"apm-*-transaction\\\" " +
//                "where LCASE(service.name) like '%"+serviceName.toLowerCase()+"%' " +"and \\\"@timestamp\\\" >= CURRENT_TIMESTAMP() - INTERVAL '15' MINUTE "+
//                "order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" }";
//        return sql;
//    }

    /**
     * 公共方法_根据serviceName获取服务ContainerId下拉框sql
     */
    public static String getServiceContainerId(String serviceName){
        String sql =   "{\"query\": \"select " +
                "container.id " +
                "FROM \\\"apm-*-metric-*\\\" " +
                "where container.id is not null and LCASE(service.name) like '%"+serviceName.toLowerCase()+"%' " +"and \\\"@timestamp\\\" >= CURRENT_TIMESTAMP() - INTERVAL '5' MINUTE "+
                "GROUP BY container.id \",\"time_zone\":\"Asia/Shanghai\" }";
        return sql;
    }

    /**
     * tomcat应用 重启停止获取事务信息sql
     */
    public static String getTomcatAppTransaction(String serviceName){
        String sql =   "{\"query\": \"select " +
                "host.name,"+
                "container.id " +
                "FROM \\\"apm-*-transaction\\\" " +
                "where LCASE(service.name) like '%"+serviceName.toLowerCase()+"%' " +"and \\\"@timestamp\\\" >= CURRENT_TIMESTAMP() - INTERVAL '15' MINUTE "+
                "order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" }";
        return sql;
    }

    /**
     * tomcat应用 获取Tomcat安装路径sql
     */
    public static String getTomcatDeploymentPath(String serviceName,String hostName){
        String sql = "{\"query\": \"select " +
                "log.file.path "+
                "FROM \\\"filebeat-*\\\" " +
                "where server_name = '"+serviceName+"' " +" and host.hostname= '"+hostName+"' "+
                "order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\" }";
        return sql;
    }

    /**
     * 获取metricbeat信息
     */
    public static String getMetricbeat(String containerId){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "host.ip_str, " +
                "container.id " +
                "from \\\"metricbeat-*\\\"  " +
//                "from \\\"metricbeat-*\\\" where container.id = '%s' " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,containerId);
    }

    /**
     * 获取metricbeat信息
     */
    public static String getMetricbeatByContainerId(String containerId){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "host.ip_str, " +
                "container.id " +
                "from \\\"metricbeat-*\\\" where container.id = '%s' " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,containerId);
    }

    /**
     * 通过apm获取IP
     */
    public static String getIpByApmSpan(String serviceName){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                " destination.ip " +
                " from \\\"apm-*-span-*\\\" where LCASE(service.name) = '"+serviceName.toLowerCase()+"'" +
                " and destination.ip is not null " +
                " order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql);
    }

    /**
     * 获取metricbeat ip信息
     */
    public static String getIpFromMetricbeat(String containerImageName){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "host.hostname " +
                "host.ip_str, " +
                "from \\\"metricbeat-*\\\" where container.image.name like '%"+containerImageName+"%' " +
                " or container.name like '%"+containerImageName+"%' " +
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     *  获取应用请求列表(最近24小时)
     */
    public static String getApplicationRequest(){
        String sql = "{\"query\":\"select " +
                "transaction.name, " +
                "sum(transaction.duration.us)/count(1) latency_avg, " +
                "PERCENTILE(transaction.duration.us, 95,'hdr') AS \\\"95th\\\", " +
                "cast(count(1) as double)/(24*60) AS tpm " +
                "FROM  " +
                "\\\"apm-*-transaction\\\"  " +
                "where " +
                "\\\"@timestamp\\\">=CURRENT_TIMESTAMP()-interval '24' HOUR " +
                "group by transaction.name\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     *  获取链路详情接口
     */
    public static String getTransactionDetail(String traceId){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "service.name, " +
                "transaction.id, " +
                "transaction.name, " +
                "transaction.duration.us, " +
                "trace.id, " +
                "url.full, " +
                "url.domain,  " +
                "url.port, " +
                "url.scheme, " +
                "http.response.status_code, " +
                "http.request.method " +
                "FROM \\\"apm-*-transaction\\\"  " +
                "where trace.id = '" + traceId + "' " +
                "order by \\\"@timestamp\\\" desc\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     *  获取链路span详情接口
     */
    public static String getSpanDetail(String traceId){
        String sql = "{\"query\": \"select " +
                "\\\"@timestamp\\\", " +
                "service.name, " +
                "transaction.id, " +
                "span.name, " +
                "span.duration.us, " +
                "span.destination.service.resource, " +
                "trace.id, " +
                "span.type, " +
                "span.subtype, " +
                "destination.address, " +
                "destination.ip, " +
                "destination.port, " +
                "event.outcome, " +
                "observer.hostname " +
                "FROM \\\"apm-*-span\\\" " +
                "where trace.id = '" + traceId + "'\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 应用监控-获取QPM
     */
    public static String getNginxQpmSql(String appAccessAddress,String startTime,String endTime,int time) throws ParseException {
        String sql = "   {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp,count(1) coutSum " +
                "from \\\"filebeat-*\\\" where event.module = 'nginx' and container.id='access.log'  and http.response.status_code  is not null and http.request.referrer='%s'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " group by timestamp  order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,appAccessAddress,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 应用监控-获取QTM
     */
    public static String getNginxTpmSql(String appAccessAddress,String startTime,String endTime,int time) throws ParseException {
        String sql = "   {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp,count(1) coutSum " +
                "from \\\"filebeat-*\\\" where event.module = 'nginx' and container.id='access.log' and http.response.status_code='200' and http.request.referrer='%s'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " group by timestamp  order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,appAccessAddress,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 应用监控-获取 请求汇总数据
     */
    public static String getRequestSummary(String appAccessAddress,String startTime,String endTime,int time) throws ParseException {
        String sql = "   {\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timestamp,count(suricata.eve.http.status=200 OR NULL) succeedSum,count(suricata.eve.http.status<>200 OR NULL) errorSum " +
                "from \\\"filebeat-*\\\" where event.module = 'nginx' and container.id='access.log'  and http.response.status_code  is not null and http.request.referrer='%s'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " group by timestamp  order by timestamp asc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,appAccessAddress,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 应用监控-获取 错误请求数据
     */
    public static String getErrorRequest(String appAccessAddress,String startTime,String endTime,int time) throws ParseException {
        String sql = "   {\"query\": \"select \\\"@timestamp\\\" timestamp, " +
                "suricata.eve.http.url errorUrl,http.response.status_code statusCode,suricata.eve.http.http_refer httpRefer " +
                "from \\\"filebeat-*\\\" " +
                "where event.module = 'nginx' and container.id='access.log'  and http.response.status_code <> '200' and http.request.referrer='%s'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,appAccessAddress,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 应用监控-获取 成功请求数据
     */
    public static String getSucceedRequest(String appAccessAddress,String startTime,String endTime,int time) throws ParseException {
        String sql = "   {\"query\": \"select \\\"@timestamp\\\" timestamp, suricata.eve.http.url errorUrl,http.response.status_code statusCode,suricata.eve.http.http_refer httpRefer " +
                "from \\\"filebeat-*\\\" where event.module = 'nginx' and container.id='access.log'  and http.response.status_code = '200' and http.request.referrer='%s'" +
                " and \\\"@timestamp\\\">=DATETIME_PARSE('%s', '%s')" +
                " and \\\"@timestamp\\\"<=DATETIME_PARSE('%s', '%s')" +
                " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql,appAccessAddress,startTime,SIMPLE_DATE_FORMAT_STR,endTime,SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 根据服务名称，获取指定时间点内的事延迟、吞吐量
     */
    public static String getServiceLatencyAndTmpSql(String serviceName,String startTime,String endTime) throws ParseException {
        String sql = "{\"query\": \"select trace.id,service.name" +
                ",transaction.type,service.environment" +
                " ,sum(transaction.duration.us)/count(1) latencyAvg " +
//                " ,count(1)/"+FullLinkSql.getTimeMinuteLength(startTime,endTime)+" as tpm"+
                " FROM \\\"apm-*-transaction\\\" where" +
                " LCASE(service.name)=LCASE('%s')" +
                getConditionTimes(startTime, endTime) +
                " group by trace.id,service.name,transaction.type,service.environment " +
                " limit 1 " +
                " \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql, serviceName);
    }
        /**
         * 根据服务名称，获取指定时间点内的事延迟、吞吐量
         */
        public static String getServiceLatencySql(String serviceName,String startTime,String endTime) throws ParseException {
            String sql = "{\"query\": \"select transaction.type,service.environment,service.name" +
                    " ,sum(transaction.duration.us)/count(1) latencyAvg " +
//                " ,count(1)/"+FullLinkSql.getTimeMinuteLength(startTime,endTime)+" as tpm"+
                    " FROM \\\"apm-*-transaction\\\" where" +
                    " LCASE(service.name)=LCASE('%s')"+
                    getConditionTimes(startTime,endTime)+
                    " group by trace.id,service.name,transaction.type,service.environment " +
                    " limit 1 " +
                    " \",\"time_zone\":\"Asia/Shanghai\"}";
            return String.format(sql,  serviceName);
    }


    /**
     * 生成获取异常日志sql方法(临时)
     */
    public static String getAllFileBeatLogs(Integer time, String startTime, String endTime){
        String where =  getConditionTimes(String.valueOf(time), startTime, endTime);
        String sql = "{\"query\": \"select \\\"@timestamp\\\" timestamp, message " +
                " from \\\"filebeat-*\\\" where container.id = 'error.log'" +
                where +
                " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return String.format(sql);
    }


    /**
     * 查询指定服务的错误日志-按分钟分段分组查询
     */
    public static String getLogCountSql(String errorGroupingKey,String time,String unit,String startTime,String endTime) throws ParseException {
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(Integer.parseInt(time),startTime,endTime)+" minute) as timeFrame," +
                " count(container.id = 'access.log' or null) as accessNumber," +
                " count(container.id = 'error.log' or null) as errorNumber " +
                " from \\\"filebeat-*\\\" where " +
                getConditionTimes(time,unit,startTime,endTime)+
                " group by timeFrame \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 按时间分组查询所有服务的日志次数sql
     *
     */
    public static String getAllServiceLogsCountSql(String startTime, String endTime, int time) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String sql = "{\"query\": \"select LCASE(service.name) serviceName, " +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame, " +
                "count(1) logNumber from \\\"apm-*\\\" " +
                "where 1=1 "+
                where+
                " group by serviceName,timeFrame\" ,\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 按时间分组查询所有服务的错误日志次数sql
     */
    public static String getAllServiceErrorLogsCountSql(String startTime, String endTime, int time) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String sql = "{\"query\": \"select LCASE(service.name) serviceName, " +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame, " +
                "count(1) errorLogNumber from \\\"apm-*-error\\\" " +
                "where 1=1 "+
                where+
                " group by serviceName,timeFrame\" ,\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 获取所有服务的容器数
     */
    public static String getServicesContainerCountSql() throws ParseException {
        String sql =   "{\"query\": \"select " +
                "LCASE(service.name) serviceName , container.id containerId " +
                "from \\\"apm-*-metric-*\\\" " +
                "where container.id is not null and \\\"@timestamp\\\" >= CURRENT_TIMESTAMP() - INTERVAL '5' MINUTE "+
                "group by serviceName, containerId \",\"time_zone\":\"Asia/Shanghai\" }";
        return sql;
    }

    /**
     * 按时间分组查询服务的日志次数sql
     * ps : serviceNameList需要在使用此方法前判断非空
     */
    public static String getAllLogsCountSql(String startTime, String endTime, int time) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame, " +
                "count(1) logNumber from \\\"apm-*\\\" " +
                "where 1=1 "+
                where+
                " group by timeFrame\" ,\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 按时间分组查询所有错误日志次数sql
     * ps : serviceNameList需要在使用此方法前判断非空
     */
    public static String getAllErrorLogsCountSql(String startTime, String endTime, int time) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame, " +
                "count(1) errorLogNumber from \\\"apm-*-error\\\" " +
                "where 1=1 "+
                where+
                " group by timeFrame\" ,\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 按时间分组查询指定条件的错误次数sql
     */
    public static String getErrorEChartSql(String startTime, String endTime, int time, String serviceName, String containerId) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String serviceNameWhere = "";
        if (StringUtils.isNotEmpty(serviceName)){
            serviceNameWhere = " and LCASE(service.name) = LCASE('"+serviceName+"')";
        }
        String containerIdWhere = "";
        if (StringUtils.isNotEmpty(containerId)){
            containerIdWhere = " and container.id = '"+containerId+"' ";
        }
        String sql = "{\"query\": \"select " +
                "histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame, " +
                "count(1) errorLogNumber from \\\"apm-*-error\\\" " +
                "where 1=1 "+
                serviceNameWhere+
                containerIdWhere+
                where+
                " group by timeFrame\" ,\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询指定服务的错误日志
     */
    public static String getErrorLogsSql(int time,String startTime,String endTime,String serviceName,String containerId) throws ParseException {
        String where =  getConditionTimes(String.valueOf(time),startTime,endTime);
        String serviceNameWhere = "";
        if (StringUtils.isNotEmpty(serviceName)){
            serviceNameWhere = " and LCASE(service.name) = LCASE('"+serviceName+"')";
        }
        String containerIdWhere = "";
        if (StringUtils.isNotEmpty(containerId)){
            containerIdWhere = " and container.id = '"+containerId+"' ";
        }
        String sql = "{\"query\": \"select histogram(\\\"@timestamp\\\",interval "+FullLinkSql.getTimeLength(time,startTime,endTime)+" minute) as timeFrame," +
                "error.grouping_key as errorGroupingKey,count(1) errorNumber," +
                " last(\\\"@timestamp\\\",timestamp.us) timestamp," +
                " last(error.grouping_name,timestamp.us) name,last(error.culprit,timestamp.us) errorCulprit" +
                " from \\\"apm-*-error\\\" where 1=1 " +
                serviceNameWhere+
                containerIdWhere+
                where+
                " group by timeFrame,error.grouping_key order by timeFrame,errorNumber desc  \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }


    /**
     * 查询全部系统盘使用信息
     */
    public static String getAllSystemDisk(Map<String, String> times){
        String sql = "{\"query\":\"select " +
                "host.ip_str, "+
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.fsstat.total_size.used, " +
                "system.fsstat.total_size.total " +
                "from \\\"metricbeat-*\\\" where " +
                "event.dataset = 'system.fsstat' " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "group by host.ip_str, \\\"@timestamp\\\" , host.name, system.fsstat.total_size.used, system.fsstat.total_size.total  "+
                "order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询全部cpu信息
     */
    public static String getAllCpuMetricbeat(Map<String, String> times){
        String sql = "{\"query\": \"select " +
                "host.ip_str, "+
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.cpu.total.norm.pct, " +
                "system.cpu.total.pct, " +
                "system.cpu.cores " +
                "from \\\"metricbeat-*\\\" where  " +
                "event.dataset = 'system.cpu'  " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "group by host.ip_str, \\\"@timestamp\\\", host.name, system.cpu.total.norm.pct, system.cpu.total.pct, system.cpu.cores "+
                "order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

    /**
     * 查询全部内存信息
     */
    public static String getAllRamMetricbeat(Map<String, String> times){
        String sql = "{\"query\":\"select  " +
                "host.ip_str, "+
                "\\\"@timestamp\\\", " +
                "host.name, " +
                "system.memory.actual.used.bytes, " +
                "system.memory.total " +
                "from \\\"metricbeat-*\\\" where  " +
                "event.dataset = 'system.memory'  " +
                "and \\\"@timestamp\\\">=DATETIME_PARSE('" + times.get("start") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "and \\\"@timestamp\\\"<=DATETIME_PARSE('" + times.get("end") + "', 'yyyy-MM-dd HH:mm:ss') " +
                "group by host.ip_str, \\\"@timestamp\\\", host.name, system.memory.actual.used.bytes, system.memory.total "+
                "order by \\\"@timestamp\\\" desc limit 1\",\"time_zone\":\"Asia/Shanghai\"}";
        return sql;
    }

}
