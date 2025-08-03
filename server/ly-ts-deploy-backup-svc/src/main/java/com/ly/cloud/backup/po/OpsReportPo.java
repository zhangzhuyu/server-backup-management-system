package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 运维报告信息表：ly_or_ops_report
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_or_ops_report")
public class OpsReportPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 时间周期(枚举)(0：周、1：月，2：年，3：自定义时间段)
     */
    @TableField(value = "time_period")
    private String timePeriod;

    /**
     * 报告名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 报告类型(枚举)【wordpdf】
     */
    @TableField(value = "report_type")
    private String reportType;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 文件保存地址
     */
    @TableField(value = "file_address")
    private String fileAddress;

    /**
     * 报告下载次数
     */
    @TableField(value = "download_count")
    private String downloadCount;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private java.util.Date endTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}

