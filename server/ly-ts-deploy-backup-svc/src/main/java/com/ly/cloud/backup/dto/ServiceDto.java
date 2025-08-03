package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务信息表
 * @author chenguoqing
 *
 */
@Data
public class ServiceDto implements Serializable {

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
     * 服务英文名称
     */
    @ApiModelProperty("服务英文名称")
    private String englishName;
    
     /**
     * 服务中文名称
     */
     @ApiModelProperty("服务中文名称")
    private String chineseName;
    
     /**
     * 应用ID（外键）
     */
     @ApiModelProperty("应用ID（外键）")
    private String applicationId;
    
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
     * 配置文件所在服务器id
     */
    @ApiModelProperty("配置文件所在服务器id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;

    /**
     * 配置文件路径
     */
    @ApiModelProperty("配置文件路径")
    private String configurationPath;

     /**
     * 健康状态（枚举）
     */
     @ApiModelProperty("健康状态（枚举）")
    private String healthStatus;

    /**
     * 服务来源（1：apm,2:本地）
     */
    @ApiModelProperty("服务来源（1：apm,2:本地）")
    private String source;

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
     * 所属应用IDs（外键）
     */
    @ApiModelProperty("所属应用IDs（外键）")
    private List<Long> applicationIds;
    

}
