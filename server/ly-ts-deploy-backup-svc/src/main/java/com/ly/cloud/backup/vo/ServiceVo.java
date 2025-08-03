package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 服务信息表
 * @author chenguoqing
 *
 */
@Data
public class ServiceVo implements Serializable {

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
     * 服务英文名称
     */
    private String englishName;
    
     /**
     * 服务中文名称
     */
    private String chineseName;
    
     /**
     * 应用ID（外键）
     */
    private String applicationId;

    /**
     * 关联应用数量
     */
    private Integer applicationNum;
    
     /**
     * 关联数据库（可多选）
     */
    private String databaseId;
    
     /**
     * 部署方式（中间件）（枚举）
     */
    private String deploymentWay;

    /**
     * 配置文件所在服务器id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;

    /**
     * 配置文件路径
     */
    private String configurationPath;

     /**
     * 健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 服务来源（1：apm,2:本地）
     */
    private String source;

     /**
     * 操作时间
     */
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
    private String operatorId;
    
     /**
     * 备注
     */
    private String remark;

    /**
     * 应用ID集合
     */
    private List<String> applicationIds;

    /**
     * IP地址(ipv4)
     */
    private String ipv4;

    /**
     * 端口
     */
    private String port;

    /**
     * 账号
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * hostname
     */
    private String hostname;

    /**
     * 错误率
     */
    private String errorRate;

    /**
     * tpm
     */
    private String tpm;

    /**
     * 延迟
     */
    private String latencyAvg;

    /**
     * tpm
     */
    private List<List<String>> tpmChart;

    /**
     * 延迟
     */
    private List<List<String>> latencyAvgChart;

    /**
     * 错误率
     */
    private List<List<String>> errorRateChart;

    /**
     * 链路ID
     */
    private String traceId;

    /**
     * 服务的运行环境
     */
    private String serviceEnvironment;

    /**
     * 事务类型
     */
    private String transactionType;

    /**
     * 应用IDs
     */
    private Long applicationId2;

}
