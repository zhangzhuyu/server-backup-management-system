package com.ly.cloud.backup.common.elasticsql;
/**
 *  监控面板docker 监控指标sql
 * */
public class PanelDockerSql {
    //ip,状态 状态指标暂缺 --不需要MessageFormat.format 所以不需要'{'中的单引号
    public static String StatusIPDockerSql="{\"query\":\"select host.ip_str ip " + //, status  \"@timestamp\",
            " from \\\"metricbeat-*\\\" " +
            " where event.module='docker' " +
            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+
            " group by ip  "+ //\"@timestamp\",
            " \",\"time_zone\":\"Asia/Shanghai\" }"; // order by \"@timestamp\" desc
    //概览--容器数量(停止/暂停/运行) cpu告警数 内存告警数，网络IO告警数(告警数先不管）
    public static String SummaryDockerSql0 ="'{'\"query\":\"select \\\"@timestamp\\\",service.address ip,  docker.info.containers.running running, docker.info.containers.paused paused, docker.info.containers.stopped stopped, docker.info.containers.total total, docker.cpu.total.pct pct" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and docker.info.containers.running >0 "+ //过滤掉null值就可以得到2023.01.16
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+ //因为采集很慢，有时间限制可能查不到数据
            " group by \\\"@timestamp\\\", ip,running ,paused ,stopped, total, pct"+
            " order by \\\"@timestamp\\\" desc limit 1 \",\"time_zone\":\"Asia/Shanghai\" '}'";
    public static String SummaryDockerSql1="'{'\"query\":\"select \\\"@timestamp\\\",service.address ip, docker.info.containers.total total,docker.cpu.total.pct pct" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and (docker.info.containers.total is not null or  docker.cpu.total.pct>0) "+ //过滤掉null值就可以得到2023.01.16
//            " and  \\\"@timestamp\\\">=current_timestamp() - interval '15' MINUTE "+ //因为采集很慢，有时间限制可能查不到数据
            " group by \\\"@timestamp\\\", ip,total,pct "+
            " order by \\\"@timestamp\\\" desc \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //DOCKER ECS节点  ---窗口名。。列表
    public static String DockerEcsNodesDockerSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10 second ) timestamp,docker.cpu.total.pct cpupct, docker.diskio.total diskio,docker.memory.usage.pct memorypct, docker.memory.rss.total memoryTotal, container.name name" + //,container.id id
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " and (docker.cpu.total.pct  is not null or docker.diskio.total is not null or docker.memory.usage.pct is not null or docker.memory.rss.total is not null ) " +
            "  group by timestamp, cpupct,diskio,memorypct,memoryTotal,name "+ //,id id,
            " {3}   \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //DOCKER ECS节点  ---改成每个指标单查，先全倒序，然后再程序排序
    public static String DockerEcsNodesDockerSql_REV="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval 10 second ) timestamp,{3} {4}, container.name name" + // docker.diskio.total diskio,docker.memory.usage.pct memorypct, docker.memory.rss.total memoryTotal,
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " and  {3}  is not null" + //(docker.cpu.total.pct  is not null or docker.diskio.total is not null or docker.memory.usage.pct is not null or docker.memory.rss.total is not null )
            " group by timestamp, {4} ,name "+ //,diskio,memorypct,memoryTotal,
            " order by {4} desc \",\"time_zone\":\"Asia/Shanghai\" '}'";

    public static String DockerEcsNodeContainerNum="'{'\"query\":\"select  container.name name,container.id id" +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by name,id "+
            " \",\"time_zone\":\"Asia/Shanghai\" '}'";

    //主机容器比例 --饼图  不明白是什么意思，也找不到相关指标 2023.1.13
    public static String HostContainerPctDockerSql="'{'\"query\":\"select \\\"@timestamp\\\" timestamp, agent.hostname hostname, container.id containerId" + //container.image.name image,
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, hostname, containerId "+ //image,
            " order by timestamp, hostname, containerId desc  \",\"time_zone\":\"Asia/Shanghai\" '}'"; //image,
    //容器镜像 -- 饼图
    public static String ContainerImageDockerSql="'{'\"query\":\"select \\\"@timestamp\\\" timestamp,agent.hostname hostname,container.image.name name" + // ,docker.image.size.regular size, docker.container.labels.com_docker_swarm_service_id serviceId
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
            " and host.ip_str like ''{0}'' "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by \\\"@timestamp\\\",hostname, name"+ //,serviceId,size
            " order by \\\"@timestamp\\\" desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //CPU使用率 --多折线图
    public static String CpuPctDockerSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,container.name name, docker.cpu.total.pct pct  " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
//            " and metricset='''cpu''' "+
            " and host.ip_str like ''{0}'' "+
            " and docker.cpu.total.pct is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, name, pct "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //内存使用率
    public static String MemoryUsageDockerSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,container.name name, docker.memory.usage.pct pct  " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
//            " and metricset='''memory''' "+
            " and host.ip_str like ''{0}'' "+
            " and docker.memory.usage.pct is not null "+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, name, pct "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";
    //网络IO
    public static String NetworkIODockerSql="'{'\"query\":\"select histogram(\\\"@timestamp\\\",interval '10' second) timestamp,container.name name, \\\"docker.network.in.bytes\\\" inByte , \\\"docker.network.out.bytes\\\" out  " +
            " from \\\"metricbeat-*\\\" " +
            " where event.module='''docker''' " +
//            " and metricset='''network''' "+
            " and host.ip_str like ''{0}'' "+
            " and (\\\"docker.network.in.bytes\\\" is not null or \\\"docker.network.out.bytes\\\" is not null)"+
            " and \\\"@timestamp\\\">= DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\"<= DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'') " +
            " group by timestamp, name, inByte,out "+
            " order by timestamp desc  \",\"time_zone\":\"Asia/Shanghai\" '}'";

    /**
     * container运行状态
     */
    public static final String containerStatusSql="'{'\"query\": \"select " +
            " container.name,container.id," +
//            " histogram(\\\"@timestamp\\\",interval {3} minute) timestamp,container.name,container.id," +
            " docker.container.status,container.runtime,docker.container.created" +
//            " container.id,docker.container.status,container.runtime,docker.container.created" +
            " from \\\"metricbeat-*\\\" where " +
            " event.module=''docker'' and metricset.name=''container'' "+
            " and  docker.container.status != '''' "+
            " and  container.id in {0}"+
            " and \\\"@timestamp\\\">=DATETIME_PARSE(''{1}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " and \\\"@timestamp\\\" <=DATETIME_PARSE(''{2}'', ''yyyy-MM-dd HH:mm:ss'')" +
            " group by container.name,container.id,docker.container.status," +
            " container.runtime,docker.container.created \",\"time_zone\":\"Asia/Shanghai\"'}'";
}


