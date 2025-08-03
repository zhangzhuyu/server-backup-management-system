package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("折线图数据vo")
@Data
public class LineDataVo {
    @ApiModelProperty("值")
    private Object[] value;
    @ApiModelProperty("是否异常")
    private Boolean abnormal;
}
