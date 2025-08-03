package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 流量监控列表
 * @author jiangzhongxin
 */
@Data
public class NetrafficVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -4759946285608607800L;

    /**
     * 主键
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * NginxId
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long nginxId;

    /**
     * Nginx反向代理地址
     */
    private String agentAddress;

    /**
     * 访问IP地址
     */
    private String accessIPAdress;

    /**
     * 应用访问地址
     */
    private String appAccessAddress;

    /**
     * url
     */
    private String url;

    /**
     * 请求状态码
     */
    private String requestCode;

    /**
     * 异常描述
     */
    private String abnormalDescription;

    /**
     * qps/tps
     */
    private String qpsOrTps;

    /**
     * 最近异常请求时间
     */
    private String abnormalRequestTime;

    /**
     * 告警URL
     */
    private String alarmURL;

}