package com.ly.cloud.backup.controller;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.PredictionResultDto;
import com.ly.cloud.backup.dto.StrategySelectionDto;
import com.ly.cloud.backup.service.CapacityPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/capacity")
public class CapacityPredictionController {
    @Autowired
    private CapacityPredictionService capacityPredictionService;

    @GetMapping("/prediction/server/{serverId}")
    public WebResponse<?> getPredictionForServer(@PathVariable String serverId) {
        try {
            return new WebResponse<>().success(capacityPredictionService.getPredictionForServer(serverId));
        } catch (Exception e) {
            // Log the error
            return new WebResponse<>().failure("获取容量预测数据时发生内部错误：" + e.getMessage());
        }
    }
}
