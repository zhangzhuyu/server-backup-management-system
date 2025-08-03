package com.ly.cloud.backup.service;

import com.ly.cloud.backup.dto.DashboardStatsDto;

public interface DashboardService {
    /**
     * 获取监控大屏的统计数据
     * @return DashboardStatsDto
     */
    DashboardStatsDto getDashboardStats();
}
