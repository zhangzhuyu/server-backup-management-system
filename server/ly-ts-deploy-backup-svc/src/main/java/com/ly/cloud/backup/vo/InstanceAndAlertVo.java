package com.ly.cloud.backup.vo;

import lombok.Data;

@Data
public class InstanceAndAlertVo {
    private String name;
    private Integer instance;
    private Iterable alert;
}
