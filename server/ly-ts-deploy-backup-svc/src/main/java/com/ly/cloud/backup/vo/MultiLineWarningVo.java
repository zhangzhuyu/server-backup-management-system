package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("单拆线带预警值数据")
@Data
public class MultiLineWarningVo {
    @ApiModelProperty("阈值dto")
    private String threshold;
//    private PanelThresholdDto threshold;
    @ApiModelProperty("折线数据")
    private List<LineChartVo> lineChartVos;
    @ApiModelProperty("额外统计数据")
    private Object extraSummary;

    //如果有一条线是异常的，就不设置为空 --2023-04-19
    public void resetThreshold(Boolean ok){
        if(ok) return;
        this.threshold=null;
    }
}
