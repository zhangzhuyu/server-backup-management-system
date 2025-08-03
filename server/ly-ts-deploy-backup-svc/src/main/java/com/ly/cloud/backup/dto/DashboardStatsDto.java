package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "监控大屏顶部统计数据")
public class DashboardStatsDto {

    @ApiModelProperty(value = "监控的数据库总数")
    private Long databasesMonitored;

    @ApiModelProperty(value = "监控的服务器总数")
    private Long serversMonitored;

    @ApiModelProperty(value = "今日完成的备份数")
    private Long backupsToday;

    @ApiModelProperty(value = "今日失败的备份数")
    private Long failuresToday;
}

