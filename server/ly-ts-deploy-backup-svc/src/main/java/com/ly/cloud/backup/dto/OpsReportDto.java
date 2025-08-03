package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 运维报告信息表
 *
 * @author chenguoqing
 */
@Data
public class OpsReportDto extends BaseDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 时间周期(枚举)(0：周、1：月，2：年，3：自定义时间段)
     */
    @ApiModelProperty("时间周期(枚举)(0：周、1：月，2：年，3：自定义时间段)")
    private String timePeriod;

    /**
     * 报告名称
     */
    @ApiModelProperty("报告名称")
    private String name;

    /**
     * 报告类型(枚举)【wordpdf】
     */
    @ApiModelProperty("报告类型(枚举)【wordpdf】")
    private String reportType;

    /**
     * 模板文件id
     */
    @ApiModelProperty("template_file_id")
    private String templateFileId;

    /**
     * 文件id
     */
    @ApiModelProperty("file_id")
    private String fileId;

    /**
     * 文件保存地址
     */
    @ApiModelProperty("file_address")
    private String fileAddress;

    /**
     * 报告下载次数
     */
    @ApiModelProperty("download_count")
    private String downloadCount;

    /**
     * 开始时间
     */
    @ApiModelProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private java.util.Date endTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


    /**
     * 自定义属性
     */

    /**
     * 报告类型
     */
    private int objectType;

    /**
     * 报告Id集合
     */
    private List<String> opsReportIds;

    // cron表达式
    @ApiModelProperty("cron表达式")
    private String cron;

}
