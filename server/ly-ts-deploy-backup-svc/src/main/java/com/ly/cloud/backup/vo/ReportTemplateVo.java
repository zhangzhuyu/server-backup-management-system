package com.ly.cloud.backup.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 模板报告
 * @TableName ly_or_report_template
 */
@Data
public class ReportTemplateVo implements Serializable {

    private static final long serialVersionUID = -5852559657202184759L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 上传人id
     */
    private String uploaderId;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private Date uploadTime;

    /**
     * 操作状态
     */
    private String operationState;

    /**
     * 默认模板(1:默认,0:不默认)
     */
    private String defaultTemplate;

    /**
     * 是否启用(1:启用,0:不启用)
     */
    private String enable;

    /**
     * 创建人ID
     */
    private String createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 备注
     */
    private String remark;

}