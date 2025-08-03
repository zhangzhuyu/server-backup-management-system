package com.ly.cloud.backup.dto;

import lombok.Data;

import java.util.List;

@Data
public class MetricsDataDto {
    // 每一个数据点都是一个数组：[时间戳, 值]
    private List<Object[]> cpuUsage;
    private List<Object[]> memoryUsage;
    private List<Object[]> diskIO;
    private List<Object[]> networkIn;
    private List<Object[]> networkOut;
}
