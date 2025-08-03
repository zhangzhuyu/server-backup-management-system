package com.ly.cloud.backup.vo;


import io.swagger.annotations.ApiModel;
import lombok.Data;


@ApiModel("单拆线带预警值数据")
@Data
public class SingleLineWarningVo {
    private Double threshold;
    private LineChartVo data;
}
