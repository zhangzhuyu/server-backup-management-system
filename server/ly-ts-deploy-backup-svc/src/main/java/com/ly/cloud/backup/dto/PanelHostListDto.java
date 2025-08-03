package com.ly.cloud.backup.dto;

import lombok.Data;

import java.util.List;

@Data
public class PanelHostListDto {
    private Long page;
    private Long pageSize;
    private List<String> fields;
    private String[] startAndEndTime;
    private String searchKey;
}
