package com.ly.cloud.backup.vo;

import com.ly.cloud.backup.dto.PanelThresholdDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("有直方型和拆线复合图")
@Data
public class DualYVo {
    @ApiModelProperty("阈值dto")
    private PanelThresholdDto threshold;
    @ApiModelProperty("折线数据")
    private List<PanelDuelYVo> lineChartVos;
    @ApiModelProperty("额外统计数据")
    private Object extraSummary;
}
