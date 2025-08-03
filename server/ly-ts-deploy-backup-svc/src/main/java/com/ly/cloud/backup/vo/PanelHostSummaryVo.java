package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 主机监控概览
 * */
@Data
@ApiModel("主机cpu/内存等概览")
public class PanelHostSummaryVo {
    @ApiModelProperty(value = "cpu核数")
    private Integer coresTotal;
    @ApiModelProperty(value = "cpu使用")
    private String cpuUsed;

    @ApiModelProperty(value = "内存总数GB")
    private String memoryTotalGB;
    @ApiModelProperty(value = "内存使用率")
    private String memoryUsage;

    @ApiModelProperty(value = "硬盘总量GB")
    private String diskTotalGB;
    @ApiModelProperty(value = "硬盘使用率")
    private String diskUsdGB;

    @ApiModelProperty(value = "紧急预警")
    private Integer criticalAlert;
    @ApiModelProperty(value = "严重预警")
    private Integer majorAlert;
    @ApiModelProperty(value = "次要预警")
    private Integer minorAlert;
    @ApiModelProperty(value = "提醒预警")
    private Integer remindAlert;
    @ApiModelProperty(value = "最后更新时间")
    private String updateTime;
}
