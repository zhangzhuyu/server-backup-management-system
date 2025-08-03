package com.ly.cloud.backup.service;

import com.ly.cloud.backup.dto.CapacityPredictionDto;
import com.ly.cloud.backup.dto.PredictionResultDto;
import com.ly.cloud.backup.dto.StrategySelectionDto;

import java.util.List;

public interface CapacityPredictionService {

    CapacityPredictionDto getPredictionForServer(String serverId);
}
