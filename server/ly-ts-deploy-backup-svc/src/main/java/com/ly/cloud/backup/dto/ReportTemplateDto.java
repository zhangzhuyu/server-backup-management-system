package com.ly.cloud.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板报告
 * @TableName ly_or_report_template
 */
@Data
public class ReportTemplateDto implements Serializable {

    private static final long serialVersionUID = -5852559657202184759L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 模板名称
     */
    @ApiModelProperty("模板名称")
    private String templateName;

    /**
     * 上传人id
     */
    @ApiModelProperty("上传人id")
    private String uploaderId;

    /**
     * 上传时间
     */
    @ApiModelProperty("上传时间")
    private Date uploadTime;

    /**
     * 操作状态
     */
    @ApiModelProperty("操作状态")
    private String operationState;

    /**
     * 默认模板(1:默认,0:不默认)
     */
    @ApiModelProperty(value = "默认模板(1:默认,0:不默认)")
    private String defaultTemplate;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    @ApiModelProperty("是否启用(1:启用,0:不启用)")
    private String enable;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    private String createId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    // 自增部分
    /**
     * 搜索内容
     */
    @ApiModelProperty("搜索内容")
    private String content;

}