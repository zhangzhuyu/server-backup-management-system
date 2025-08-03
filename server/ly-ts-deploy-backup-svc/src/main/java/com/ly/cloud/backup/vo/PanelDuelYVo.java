package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("有直方型和拆线复合图")
@Data
public class PanelDuelYVo {
        @ApiModelProperty("图类型， bar或line型")
        private String type;

        @ApiModelProperty("echart lengend 折线名称")
        private String name;

        @ApiModelProperty("数据")
        private List<LineDataVo> data;

        @ApiModelProperty("是否异常")
        private Boolean abnormal;
}
