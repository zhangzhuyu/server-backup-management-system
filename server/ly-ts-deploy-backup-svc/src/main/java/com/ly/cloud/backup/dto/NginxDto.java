package com.ly.cloud.backup.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * nginx信息表
 * @author chenguoqing
 *
 */
@Data
public class NginxDto implements Serializable {

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
     * 应用名称
     */
     @ApiModelProperty("应用名称")
    private String name;
    
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
     * 部署方式（中间件）（枚举）
     */
     @ApiModelProperty("部署方式（中间件）（枚举）")
    private String deploymentWay;
    
     /**
     * 部署路径
     */
     @ApiModelProperty("部署路径")
    private String deploymentPath;

    /**
     * 健康状态
     */
    @ApiModelProperty("健康状态")
    private String healthStatus;

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
     * 序号
     */
    @ApiModelProperty("序号")
    private String serialNumber;

    /**
     * 访问路径
     */
    @ApiModelProperty("访问路径")
    private String path;

    /**
     * 文件名称
     */
    @ApiModelProperty("文件名称")
    private String fileName;

    /**
     * 新文件名称
     */
    @ApiModelProperty("新文件名称")
    private String fileNewName;

    /**
     * 文件内容
     */
    @ApiModelProperty("文件内容")
    private String fileContent;

    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}
