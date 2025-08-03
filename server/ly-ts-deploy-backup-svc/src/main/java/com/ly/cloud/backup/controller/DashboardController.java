package com.ly.cloud.backup.controller;

import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.DashboardStatsDto;
import com.ly.cloud.backup.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dashboard")
@Api(tags = "监控大屏接口")
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @ApiOperation("获取顶部统计卡片数据")
    public WebResponse<DashboardStatsDto> getStats() {
        try {
            DashboardStatsDto stats = dashboardService.getDashboardStats();
            return new WebResponse<DashboardStatsDto>().success(stats);
        } catch (Exception e) {
            // 在实际项目中，这里应该有更完善的日志记录
            return new WebResponse<DashboardStatsDto>().failure("Failed to get dashboard stats: " + e.getMessage());
        }
    }
}
