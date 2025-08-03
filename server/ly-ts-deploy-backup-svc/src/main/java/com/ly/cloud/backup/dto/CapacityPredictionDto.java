package com.ly.cloud.backup.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CapacityPredictionDto {
    private ServerCapacityInfoDto serverInfo;
    private PredictionResultDto predictionResult;
    private List<Map<String, Object>> historicalData; // ECharts 需要的格式: [{date: '...', size: ...}]
    private boolean predictionAvailable; // 预测是否可用
    private String message; // 如果不可用，显示的消息
}
