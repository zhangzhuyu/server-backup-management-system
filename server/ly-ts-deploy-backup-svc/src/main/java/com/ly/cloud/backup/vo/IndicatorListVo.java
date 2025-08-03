package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IndicatorListVo {
    private static final long serialVersionUID = 4201580187474756619L;

    private String title;

    private Integer status;

    private Map<String,String> indicatorTestItems;

    private List<IndicatorListVo> subIndicatorItem;
}
