package com.ly.cloud.backup.common.constant;



/**
 * Elastic常量
 *
 * @author: wzz
 */
public class ElasticConstant {

    /**
     * 组件类型：nginx
     */
    public static final String COMPONENT_TYPE_NGINX = "nginx";

    /**
     * 组件类型：数据库mysql
     */
    public static final String COMPONENT_TYPE_MYSQL = "mysql";

    /**
     * 组件类型：linux(system)
     */
    public static final String COMPONENT_TYPE_SYSTEM = "system";

    /**
     * 组件类型：java
     */
    public static final String COMPONENT_TYPE_JAVA = "java";

    /**
     * 组件类型：iptables
     */
    public static final String COMPONENT_TYPE_IPTABLES = "iptables";

    /**
     * 组件类型：ips(iptables中包含"table"，前端传参时sql过滤报错，改为传ips)
     */
    public static final String COMPONENT_TYPE_IPS = "ips";

    /**
     * 日志类型：错误、异常日志
     */
    public static final String LOG_TYPE_ERROR = "error";

    /**
     * 日志类型：原始日志
     */
    public static final String LOG_TYPE_ORIGIN = "origin";


}
