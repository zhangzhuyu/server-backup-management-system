package com.ly.cloud.backup.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServerCapacityInfoDto {
    private String serverName;
    private String serverIp;
    private BigDecimal totalCapacityGB; // 总容量 (GB)
    private BigDecimal usedCapacityGB;  // 已用容量 (GB)
    private int usagePercentage;        // 使用率百分比
}
