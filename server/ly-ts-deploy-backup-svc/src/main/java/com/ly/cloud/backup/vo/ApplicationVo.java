package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 应用信息表
 * @author chenguoqing
 *
 */
@Data
public class ApplicationVo implements Serializable {

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
     * 业务领域（枚举）
     */
    private String businessDomain;

    /**
     * 应用英文名称
     */
    private String englishName;

    /**
     * 应用中文名称
     */
    private String chineseName;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 关联服务器（可多选）
     */
    private String serverId;

    /**
     * 关联数据库（可多选）
     */
    private String databaseId;

    /**
     * 部署方式（中间件）（枚举）
     */
    private String deploymentWay;

    /**
     * 是否加入运维大屏展示（枚举）
     */
    private String whetherMonitoring;

    /**
     * 是否初次运行（枚举）
     */
    private String initialOperation;

    /**
     * 健康状态（枚举）
     */
    private String healthStatus;

    /**
     * 健康检测地址
     */
    private String healthMonitoringUrl;

    /**
     * 序号
     */
    private String serialNumber;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    private String operatorId;


    /**
     * 所属公司
     */
    private Long affiliatedCompany;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部署路径
     */
    private String deploymentPath;

    // 自增属性
    /**
     * 部署ip地址
     */
    private String deploymentIp;

    /**
     * 分配服务ids
     */
    private List<ApplicationServiceVo> services;

    /**
     * 分配服务ids
     */
    private List<String> serviceIds;

    /**
     * 服务总数
     */
    private int serviceSum;

    /**
     * 异常服务数
     */
    private int abnormalServiceCount;

    /**
     * 正常服务数
     */
    private int normalServiceCount;

    /**
     * 服务children
     */
    private List<ServiceVo> children;

    /**
     * tpm
     */
    private String tpm;
    private List<List<String>> tpmChart;

    /**
     * 延迟
     */
    private String latencyAvg;

    /**
     * 延迟
     */
    private List<List<String>> latencyAvgChart;

    /**
     * 错误率
     */
    private String errorRate;
    private List<List<String>> errorRateChart;

    /**
     * logo
     */
    private String logo;

    /**
     * NGINX IP
     */
    private String nginxIp;

    /**
     * 容器总数
     */
    private int containerSum;

    /**
     * 日志发生次数
     */
    private int logSum;

    /**
     * 日志发生次数eChart数据
     */
    private List<Map<String,String>> logsEChartList;

    /**
     * 错误日志发生次数
     */
    private int errorLogSum;

    /**
     * 错误日志发生次数eChart数据
     */
    private List<Map<String,String>> errorLogsEChartList;
}
