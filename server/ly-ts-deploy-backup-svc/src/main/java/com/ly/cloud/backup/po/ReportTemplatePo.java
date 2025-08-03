package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板报告
 * @TableName ly_or_report_template
 */
@TableName("ly_or_report_template")
@Data
public class ReportTemplatePo implements Serializable {

    private static final long serialVersionUID = -5852559657202184759L;

    /**
     * 主键
     */
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 模板名称
     */
    @TableField(value = "template_name")
    private String templateName;

    /**
     * 上传人id
     */
    @TableField(value = "uploader_id")
    private String uploaderId;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private Date uploadTime;

    /**
     * 操作状态
     */
    @TableField(value = "operation_state")
    private String operationState;

    /**
     * 默认模板(1:默认,0:不默认)
     */
    @TableField(value = "default_template")
    private String defaultTemplate;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    @TableField(value = "enable")
    private String enable;

    /**
     * 创建人ID
     */
    @TableField(value = "create_id")
    private String createId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}