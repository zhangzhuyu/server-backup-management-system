package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@ApiModel("监控面板拆线top10排序列dto")
@Data
public class PanelTop10MapDto implements Comparable<PanelTop10MapDto> {
    @ApiModelProperty("拆线名")
    private String name;
    @ApiModelProperty("最大值")
    private Double value;

    @Override
    public int compareTo(@NotNull PanelTop10MapDto o) {
        return this.value>o.getValue()?1:(this.value==o.getValue()?0:-1);
    }
}
