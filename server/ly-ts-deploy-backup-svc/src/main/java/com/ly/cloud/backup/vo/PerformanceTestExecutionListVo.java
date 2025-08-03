package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;

@Data
public class PerformanceTestExecutionListVo {
    /**
     * 序列号
     */
    private static final long serialVersionUID = 3618647115941167333L;

    private String host;

    private String hostName;

    private List<IndicatorListVo> subIndicators;


}
