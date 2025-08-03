package com.ly.cloud.backup.vo;

import lombok.Data;

@Data
public class MonitoringPanelVo {
    private Integer code;
    private String resourcesType;
    private String name;
    private Long ruleTotal;
    private Long recordTotal;
    private String description;
}
