package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;

@Data
public class PanelHostPageVo {
    private String ip;
    private List<Object> values;
}
