package com.ly.cloud.backup.common.constant;

/**
 * Redis缓存Key常量
 *
 * @author SYC
 * @Description
 * @Date 2022-04-02
 */
public class RedisConstants {
    /**
     * 时间常量，单位秒
     * <p>
     * 一分钟、一小时、一天、一周、一月（30天）、一年
     */
    public static final long TIME_OF_MINUTE = 60;
    public static final long TIME_OF_HOUR = 3600;
    public static final long TIME_OF_DAY = 86400;
    public static final long TIME_OF_WEEK = 604800;
    public static final long TIME_OF_MONTH = 2592000;
    public static final long TIME_OF_YEAR = 31557600;

    /**
     * 平台名称：平台所有缓存的key前缀，必须拼接（保证在相同一个目录下）
     */
    public static final String REDIS_KEY_PREFIX_TC = "ly-ts-deploy-svc:";

    /**
     * 模块名称：用户登录错误次数模块（key格式：用户账号）
     */
    public static final String REDIS_KEY_PREFIX_TC_LOGIN_ERROR_MODULE = REDIS_KEY_PREFIX_TC + "login:error:";

    /**
     * 模块名称：验证码（key格式：code）
     */
    public static final String REDIS_KEY_PREFIX_TC_VERIFICATION_CODE_MODULE = REDIS_KEY_PREFIX_TC + "verification:code:";

    /**
     * 模块名称：全链路模块（key格式：平台名称+模块名称+方法名称+键名）
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_MODULE = REDIS_KEY_PREFIX_TC + "link:";

    /**
     * 模块名称：应用监控大屏模块（key格式：平台名称+模块名称+方法名称+键名）
     */
    public static final String REDIS_KEY_PREFIX_TC_APPMONITORING_MODULE = REDIS_KEY_PREFIX_TC + "appMonitoring:";

    /**
     * 模块名称：监控预警模块（key格式：监控所需指标名称）
     */
    public static final String REDIS_KEY_PREFIX_TC_TARGET_MODULE = REDIS_KEY_PREFIX_TC + "target:";

    /**
     * 模块名称：监控预警模块（key格式：上次监控时间）
     */
    public static final String REDIS_KEY_PREFIX_TC_RECORD_MODULE = REDIS_KEY_PREFIX_TC + "ruleId:";

    /**
     * 模块名称：运维监控模块（key格式：平台名称+模块名称+方法名称+键名）
     */
    public static final String REDIS_KEY_PREFIX_TC_OPERATIONS_SCREEN_MODULE = REDIS_KEY_PREFIX_TC + "operations:";

    /**
     * 模块名称：用户登录模块（key格式：平台名称+模块名称+方法名称+键名）
     */
    public static final String REDIS_KEY_PREFIX_TC_LOGIN_MODULE = REDIS_KEY_PREFIX_TC + "login:";

    /**
     * 模块名称：服务器模块（key格式：平台名称+模块名称+方法名称+键名）
     */
    public static final String REDIS_KEY_PREFIX_TC_SERVER_MODULE = REDIS_KEY_PREFIX_TC + "server:";

    /**
     * 冒号：命名空间的目录层级
     */
    public static final String COLON = ":";


    /**
     * 服务List
     */
    public static final String REDIS_KEY_PREFIX_TC_SERVICES = REDIS_KEY_PREFIX_TC_LINK_MODULE + "services";

    /**
     * 服务List
     */
    public static final String REDIS_KEY_PREFIX_TC_SERVICES_BY_TRACEID = REDIS_KEY_PREFIX_TC_LINK_MODULE + "servicesByTraceId";

    /**
     * 服务器cpu,硬盘，内存使用率信息
     */
    public static final String REDIS_KEY_PREFIX_TC_SERVER_USAGE_RATE_INFO = REDIS_KEY_PREFIX_TC_SERVER_MODULE + "serverUsageRateInfo";

    /**
     * 全链路-详情-概览-服务器数组
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_OVERVIEW_SERVER = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailOverviewServer:";

    /**
     * 全链路-详情-概览-事务列表
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_OVERVIEW_TRANSACTION = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailOverviewTransaction:";

    /**
     * 全链路-详情-概览-延迟
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_OVERVIEW_LATENCY = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailOverviewLatency:";
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_OVERVIEW_TPM = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailOverviewTpm:";
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_OVERVIEW_ERROR_RATE = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailOverviewErrorRate:";

    /**
     * 全链路-详情-服务器总量规格信息
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_DNS_SERVER = REDIS_KEY_PREFIX_TC_LINK_MODULE + "detailDnsServer:";

    /**
     * 全链路主机信息模块，包含基本信息
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_HOST = REDIS_KEY_PREFIX_TC_LINK_MODULE + "host:";


    /**
     * 全链路首页链接信息
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_HOME = REDIS_KEY_PREFIX_TC_LINK_MODULE + "home:";

    /**
     * 防火墙信息
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_FIREWALL = REDIS_KEY_PREFIX_TC_LINK_MODULE + "firewall:";

    /**
     * 挂载信息
     */
    public static final String REDIS_KEY_PREFIX_TC_LINK_DETAIL_DockerInspect = REDIS_KEY_PREFIX_TC_LINK_MODULE + "dockerInspect:";

    /**
     * 应用监控大屏中间内容信息
     */
    public static final String REDIS_KEY_PREFIX_TC_APPMONITORING_CENTER = REDIS_KEY_PREFIX_TC_APPMONITORING_MODULE + "center:";

    /**
     * 应用监控大屏数据库信息
     */
    public static final String REDIS_KEY_PREFIX_TC_APPMONITORING_DATABASE = REDIS_KEY_PREFIX_TC_APPMONITORING_MODULE + "database:";

    /**
     * 应用监控大屏 慢请求信息
     */
    public static final String REDIS_KEY_PREFIX_TC_APPMONITORING_SLOWREQUEST = REDIS_KEY_PREFIX_TC_APPMONITORING_MODULE + "SlowRequest:";

    /**
     * 备份策略下拉框信息
     */
    public static final String REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX = "backup:";

    /**
     * 备份策略下拉框数据库信息
     */
    public static final String REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_DATABASE = REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX + "database:";

    /**
     * 备份策略下拉框表格信息
     */
    public static final String REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX_TABLES = REDIS_KEY_PREFIX_TC_BACKUP_COMBOBOX + "tables:";

    /**
     * 备份策略ssh过期信息
     */
    public static final String REDIS_KEY_PREFIX_TC_BACKUP_EXPIRATION_TIMES = "backup-expiration-times:";
}
