package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用信息表
 * @author chenguoqing
 *
 */
@Data
public class ApplicationDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     @ApiModelProperty("主键")
    private Long id;

    /**
     * 业务领域（枚举）
     */
    @ApiModelProperty("业务领域（枚举）")
    private String businessDomain;

    /**
     * 应用中文名称
     */
    @ApiModelProperty("应用中文名称")
    private String chineseName;

    /**
     * 应用英文名称
     */
    @ApiModelProperty("应用英文名称")
    private String englishName;
    
     /**
     * 访问地址
     */
     @ApiModelProperty("访问地址")
    private String url;
    
     /**
     * 关联服务器（可多选）
     */
     @ApiModelProperty("关联服务器（可多选）")
    private String serverId;
    
     /**
     * 关联数据库（可多选）
     */
     @ApiModelProperty("关联数据库（可多选）")
    private String databaseId;
    
     /**
     * 部署方式（中间件）（枚举）
     */
     @ApiModelProperty("部署方式（中间件）（枚举）")
    private String deploymentWay;
    
     /**
     * 是否加入运维大屏展示（枚举）
     */
     @ApiModelProperty("是否加入运维大屏展示（枚举）")
    private String whetherMonitoring;

    /**
     * 是否初次运行（枚举）
     */
    @ApiModelProperty("是否初次运行（枚举）")
    private String initialOperation;

    /**
     * 健康状态（枚举）
     */
     @ApiModelProperty("健康状态（枚举）")
    private String healthStatus;

    /**
     * 健康检测地址
     */
    @ApiModelProperty("健康检测地址")
    private String healthMonitoringUrl;

    /**
     * 序号
     */
    @ApiModelProperty("序号")
    private String serialNumber;

     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date operationTime;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operatorId;
    
     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;

    /**
     * 分配服务
     */
    @ApiModelProperty("分配服务")
    private String serviceId;

    /**
     * 所属公司
     */
    @ApiModelProperty("所属公司")
    private Long affiliatedCompany;

    /**
     * 分配服务ids
     */
    @ApiModelProperty("分配服务")
    private List<Long> serviceIds;

    /**
     * 部署路径
     */
    @ApiModelProperty("部署路径")
    private String deploymentPath;

    /**
     * 搜索内容
     */
    private String content;
}
