package com.ly.cloud.backup.controller;


import com.ly.cloud.backup.common.WebResponse;
import com.ly.cloud.backup.dto.MetricsDataDto;
import com.ly.cloud.backup.dto.ServerInfoDto;
import com.ly.cloud.backup.service.BackupManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@Api(tags = "服务器指标监控相关接口API")
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private BackupManagementService serverService;

    /**
     * 获取可监控的服务器列表
     */
    @ApiOperation(value = "获取可监控的服务器列表" , notes = "获取可监控的服务器列表" , httpMethod = "GET")
    @RequestMapping(value = "/servers", method = RequestMethod.GET)
    public WebResponse<List<ServerInfoDto>> getServerList() {
        return new WebResponse<List<ServerInfoDto>>().success(serverService.getServerInfoList());
    }

    /**
     * 获取指定服务器的模拟指标数据
     * @param serverId 服务器ID (为了让每次请求数据不同，可以基于ID做随机种子)
     * @return 模拟的指标数据
     */
    @ApiOperation(value = "获取指定服务器的模拟指标数据" , notes = "获取指定服务器的模拟指标数据" , httpMethod = "GET")
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public WebResponse<MetricsDataDto> getMetricsData(@RequestParam String serverId) {
        MetricsDataDto data = new MetricsDataDto();
        Random random = new Random(serverId.hashCode()); // 使用ID的hashCode作为种子，让不同服务器的图表有差异

        // 生成最近三个月的数据（每天一个点，共90个点）
        List<Object[]> cpuData = new ArrayList<>();
        List<Object[]> memData = new ArrayList<>();
        List<Object[]> diskData = new ArrayList<>();
        List<Object[]> netInData = new ArrayList<>();
        List<Object[]> netOutData = new ArrayList<>();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());

        for (int i = 90; i > 0; i--) {
            // 获取i天前的时间戳
            long timestamp = now.minusDays(i).toInstant().toEpochMilli();

            // 生成模拟数据
            cpuData.add(new Object[]{timestamp, 10 + random.nextInt(70)}); // CPU使用率 10-80%
            memData.add(new Object[]{timestamp, 30 + random.nextInt(60)}); // 内存使用率 30-90%
            diskData.add(new Object[]{timestamp, 5 + random.nextInt(50)});  // 硬盘IO 5-55 MB/s
            netInData.add(new Object[]{timestamp, 1 + random.nextInt(20)});   // 网卡流入 1-21 Mbps
            netOutData.add(new Object[]{timestamp, random.nextInt(15)});  // 网卡流出 0-15 Mbps
        }

        data.setCpuUsage(cpuData);
        data.setMemoryUsage(memData);
        data.setDiskIO(diskData);
        data.setNetworkIn(netInData);
        data.setNetworkOut(netOutData);

        return new WebResponse<MetricsDataDto>().success(data);
    }
}
