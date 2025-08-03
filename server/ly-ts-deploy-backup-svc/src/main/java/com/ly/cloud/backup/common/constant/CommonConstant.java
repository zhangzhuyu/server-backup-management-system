package com.ly.cloud.backup.common.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * Class Name: CommonConstant Description: 公共常量类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月22日 15:55
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class CommonConstant {

    /**
     * 驱动名称集合
     */
    public static final String[] DRIVER_LIST = new String[]{
            "oracle.jdbc.OracleDriver",
            "oracle.jdbc.driver.OracleDriver",
            "com.mysql.jdbc.Driver",
            "com.mysql.cj.jdbc.Driver",
            "org.postgresql.Driver",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "com.ibm.db2.jcc.DB2Driver"
    };

    /**
     * nginx日志indexs
     */
    public static final String[] FILEBEAT_LOG = new String[]{
            "filebeat-*"
    };

    /**
     * docker服务名字段
     */
    public static final String DOCKER_SWARM_SERVICE_NAME_FIELD = "container.labels.com_docker_swarm_service_name";

    /**
     * nginx日志信息查询条件event.module字段
     */
    public static final String FILEBEAT_LOG_EVENT_MODULE_FIELD = "event.module";

    /**
     * nginx日志信息查询条件agent.hostname字段
     */
    public static final String FILEBEAT_LOG_AGENT_HOSTNAME_FIELD = "agent.hostname";

    /**
     * nginx日志信息查询条件host.ip字段
     */
    public static final String FILEBEAT_LOG_HOST_IP_FIELD = "host.ip";

    /**
     * nginx日志信息查询条件container.id字段
     */
    public static final String FILEBEAT_CONTAINER_ID = "container.id";

    /**
     * nginx日志信息查询条件http.request.referrer字段
     */
    public static final String HTTP_REQUEST_REFERRER_FIELD = "http.request.referrer";

    /**
     * nginx日志信息字段
     */
    public static final String FILEBEAT_LOG_MESSAGE_FIELD = "message";

    /**
     * 时间戳字段
     */
    public static final String TIMESTAMP_FIELD = "@timestamp";

    /**
     * nginx
     */
    public static final String NGINX = "nginx";

    /**
     * access.log
     */
    public static final String ACCESS_LOG  = "access.log";

    /**
     * conf
     */
    public static final String CONF = "conf";

    /**
     * 顿号
     */
    public static final String CAESURA_SIGN = "、";

    /**
     * 下划线
     */
    public static final String UNDERLINE_SIGN = "_";

    /**
     * ""
     */
    public static final String EMPTY = StringUtils.EMPTY;

    /**
     * " "
     */
    public static final String SPACE = StringUtils.SPACE;

    /**
     * 文本后缀
     */
    public static final String TEXT_SUFFIX = ".text";

    /**
     * text/plain
     */
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    /**
     * image/png
     */
    public static final String CONTENT_TYPE_IMG_PNG = "image/png";

    /**
     * 标准的编码格式
     */
    public static final String CONTENT_TYPE_APPLICATION_CHARSET = "application/x-download;charset=UTF-8";

    /**
     * 是 MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * 附加的文件参
     */
    public static final String CONTENT_DISPOSITION_VALUE = "attachment;filename=";

    /**
     * GET
     */
    public static final String GET = "GET";

    /**
     * POST
     */
    public static final String POST = "POST";

    /**
     * ENDPOINT
     */
    public static final String ENDPOINT = "/_sql?format=txt";

    /**
     * 美元符号
     */
    public static final String DOLLAR_SIGN = "$";

    /**
     * all
     */
    public static final String ALL = "all";

    /**
     * 全部
     */
    public static final String CN_ALL = "全部";

    /**
     * 一般时间格式字符
     */
    public static final String SIMPLE_DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATE_FORMAT_S_STR = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    public static final String SIMPLE_MONTH_FORMAT_STR = "yyyy-MM";
    public static final String SIMPLE_DAY_FORMAT_STR = "yyyy-MM-dd";

    /**
     * null
     */
    public static final String NULL = "null";
    public static final String APP = "app";

    /**
     * UTC时间格式字符
     */
    public static final String UTC_DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * UTC时间格式字符
     */
    public static final String ISO_DATE_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String WINDOWS = "windows";
    public static final String OS_NAME = "os.name";
    public static final String API = "api";


}
