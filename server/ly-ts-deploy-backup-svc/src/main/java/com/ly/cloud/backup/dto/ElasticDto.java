package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ElasticDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 8763700094891693074L;


    /**
     * 事务名称
     */
    @ApiModelProperty("事务名称")
    private String transactionName;

    /**
     * 服务名称
     */
    @ApiModelProperty("服务名称")
    private String serviceName;

    /**
     * 数据库类型
     */
    @ApiModelProperty("数据库类型")
    private String databaseType;

    /**
     * 请求完整路径
     */
    @ApiModelProperty("请求完整路径")
    private String urlFull;

    /**
     * 多少分钟之前
     */
    @ApiModelProperty("分钟之前")
    private Integer amount;

    /**
     * ip
     */
    @ApiModelProperty("ip")
    private String ip;

    /**
     * 链路跟踪id
     */
    @ApiModelProperty("链路跟踪id")
    private String traceId;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    private Integer time;

    /**
     * 错误日志的id
     */
    @ApiModelProperty("错误日志的id")
    private String errorGroupingKey;

    /**
     * 服务器的id
     */
    @ApiModelProperty("服务器的id")
    private String serverId;

    /**
     * 开始时间
     */
    @ApiModelProperty("startTime")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("endTime")
    private String endTime;

    /**
     * jvm垃圾回收GC类型
     */
    @ApiModelProperty("gcType")
    private int gcType;


    /**
     * 表空间类型(1：用户表空间，2：临时表空间)
     */
    @ApiModelProperty("表空间类型")
    private int tablespaceType;

    /**
     * 检索关键字
     */
    @ApiModelProperty("检索关键字")
    private String searchParam;

    /**
     * 服务名称数组
     */
    @ApiModelProperty("服务名称数组")
    private String[] serviceNames;

    /**
     * redisKeys
     */
    @ApiModelProperty("redisKeys")
    private String[] keyspaces;


    /**
     * hostname
     */
    @ApiModelProperty("hostname")
    private String hostname;

    /**
     * 链接地址
     */
    @ApiModelProperty("serviceAddress")
    private String serviceAddress;

    /**
     * 是否启用redis缓存
     */
    @ApiModelProperty("cacheChecked")
    private Boolean cacheChecked;

    /**
     * 应用id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("应用id")
    private Long applicationId;

    /**
     * 应用访问地址
     */
    @ApiModelProperty("应用访问地址")
    private String appAccessAddress;

    /**
     * 容器ID
     */
    @ApiModelProperty("containerId")
    private String containerId;

    /**
     * 健康状态
     */
    @ApiModelProperty("healthStatus")
    private Integer healthStatus;

    @ApiModelProperty("排序字段")
    private String columnKey;

    @ApiModelProperty("排序顺序")
    private String order;

}
