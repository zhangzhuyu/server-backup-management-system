package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LevelRuleNumerVo implements Serializable {
    private static final long serialVersionUID = -8169486324392945423L;

    private String warningLevel;

    private Long num;
}
