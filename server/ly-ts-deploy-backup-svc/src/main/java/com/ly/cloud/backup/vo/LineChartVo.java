package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@ApiModel("拆线图数据单元")
@Data
public class LineChartVo {
    @ApiModelProperty("echart lengend 折线名称")
    private String name;

    @ApiModelProperty("数据")
    private List<LineDataVo> data;

    @ApiModelProperty("是否异常")
    private Boolean abnormal;

    public LineChartVo example(){
        LineChartVo vo=new LineChartVo();
        vo.setName("example");

        List<LineDataVo> list=new ArrayList<>();
        LineDataVo lineDataVo=new LineDataVo();
        lineDataVo.setValue(new Object[]{"2022-11-07 00:00:00","251.82"});
        list.add(lineDataVo);

        vo.setData(list);
        return vo;
    }

}
