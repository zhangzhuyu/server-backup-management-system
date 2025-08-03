package com.ly.cloud.backup.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PredictionResultDto {
    private List<Map<String, Object>> historicalData; // 用于图表绘制的历史数据点
    private List<Map<String, Object>> predictedData; // 用于图表绘制的预测数据点
    private String predictedExhaustionDate; // 预测的耗尽日期
    private String message; // 提示信息，如"数据不足"或"容量稳定"
    private Double currentUsageMB; // 当前总使用量
    private Double growthRateMBPerDay; // 每日增长率

    private String predictedFullDate;   // 预测满盘日期
    private long daysRemaining;         // 剩余天数
    private BigDecimal dailyGrowthRateMB; // 每日增长率 (MB)

}
