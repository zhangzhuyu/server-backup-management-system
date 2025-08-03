package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;

@Data
public class OneButtonDetectionTargetSourceVo {
    private static final long serialVersionUID = -3080085992203779704L;

    private String title;
    private String value;
    private List<OneButtonDetectionTargetSourceVo> children;
}
