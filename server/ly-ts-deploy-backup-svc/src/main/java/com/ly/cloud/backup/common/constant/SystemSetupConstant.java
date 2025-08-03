package com.ly.cloud.backup.common.constant;

/**
 * @author SYC
 * @Date: 2022/7/29 13:53
 * @Description 系统变量表的key ly_sm_system_setup
 */
public class SystemSetupConstant {
    /**
     * license证书base64值存储
     */
    public final static String LICENSE = "license";

    /*
        平台图标logo--->对应数据库ly-sm-system-setup表中set_key字段中的logo
    */
    public final static String LOGO = "logo";
   /*
        系统设置--页脚
   */
    public final static String FOOTER="footer";
    /*
        系统设置---平台名称
    */
    public final static String PLATFORMNAME="platformName";

    /*
     系统设置---设置定期清楚操作日志天数
 */
    public final static String DALETELOGTIME="saveLogTime";

    /*
       系统设置---项目地名称
    */
    public final static String SCHOOLNAME="schoolName";

    /**
     * 虚拟IP段
     */
    public final static String VIRTUAL_IP_SEGMENT = "virtualIpSegment";
    /**
     * 排除的IP段
     */
    public final static String EXCLUDED_IP_SEGMENT = "excludedIpSegment";
    /**
     * 排除的固定IP地址
     */
    public final static String EXCLUDED_FIXED_IP = "excludedFixedIp";
    /**
     * 项目地名称
     */
    public final static String SCHOOL_NAME = "schoolName";

    /**
     * 全链路域名是否需要去掉别名
     */
    public final static String ALIAS = "alias";

    /**
    *应用图标logo--->对应数据库ly-sm-system-setup表中set_key字段中的appLogo
    */
    public final static String APP_LOGO = "appLogo";

    /**
     * kibana链接信息
     */
    public final static String KIBANA = "kibana";


  /**
   * taier链接信息
   */
  public final static String TAIER = "taier";
}
