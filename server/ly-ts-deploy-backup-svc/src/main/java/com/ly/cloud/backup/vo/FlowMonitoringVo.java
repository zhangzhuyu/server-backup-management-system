package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 流量监控表
 * @author chenguoqing
 *
 */
@Data
public class FlowMonitoringVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    
     /**
     * 应用id
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long applicationId;

    /**
     * 应用英文名称
     */
    private String appEnglishName;

    /**
     * 应用中文名称
     */
    private String appChineseName;

    /**
     * 应用访问地址
     */
    private String appAccessAddress;

    /**
     * 应用健康状态
     */
    private String appHealthStatus;

    /**
     * NginxId
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long nginxId;

    /**
     * nginx安装路径
     */
    private String path;

     /**
     * Nginx反向代理地址
     */
    private String agentAddress;

    /**
     * Nginx反向代理地址(location)
     */
    private String agentAddressName;
    
     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 序号
     */
    private String serialNumber;

     /**
     * 备注
     */
    private String remark;

    //自增属性

    /**
     * 服务器id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;


    /**
     * 服务id集合
     */
    private List<String> services;
    /**
     * 服务总数
     */
    private int serviceSum;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 访问IP地址
     */
    private String accessIPAdress;

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
     * qpm/tpm
     */
    private String qpmOrTpm;

    /**
     * qpm
     */
    private Double qpm;

    /**
     * tpm
     */
    private Double tpm;

    /**
     * 错误请求数
     */
    private Integer errorRequestTotal;

    /**
     * 成功请求数
     */
    private Integer successRequestTotal;

    /**
     * 请求总数
     */
    private Integer requestTotal;

    /**
     * 最近异常请求时间
     */
    private String abnormalRequestTime;

    /**
     * 告警URL
     */
    private String alarmURL;

    // 自增属性
    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * hostname
     */
    private String hostname;

    /**
     * 查询的时间转换成分钟
     */
    private Long queryMinutes;

}
