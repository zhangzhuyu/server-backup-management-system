package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用服务关系表
 * @author chenguoqing
 *
 */
@Data
public class ApplicationServiceDto implements Serializable {

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
     * 应用ID
     */
     @ApiModelProperty("应用ID")
    private Long applicationId;

    /**
     * 应用ID集合
     */
    @ApiModelProperty("应用ID集合")
    private List<Long> applicationIds;
    
     /**
     * 服务ID
     */
     @ApiModelProperty("服务ID")
    private Long serviceId;

     /**
     * 部署方式（中间件）（枚举）
     */
     @ApiModelProperty("部署方式（中间件）（枚举）")
    private String deploymentWay;

    /**
     * 所属公司
     */
    @ApiModelProperty("所属公司")
    private String affiliatedCompany;

    /**
     * 配置文件所在服务器
     */
    @ApiModelProperty("配置文件所在服务器")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long serverId;

     /**
     * 服务配置文件路径
     */
     @ApiModelProperty("服务配置文件路径")
    private String configurationPath;

     /**
     * 中文名称
     */
     @ApiModelProperty("中文名称")
    private String chineseName;
    
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
    

}
