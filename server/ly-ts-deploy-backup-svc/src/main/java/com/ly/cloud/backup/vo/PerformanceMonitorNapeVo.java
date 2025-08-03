package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PerformanceMonitorNapeVo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -4281950101609040891L;

    private Long id;

    private String napeType;

    private String napeName;

    private String parentNapeType;

    private String leafNode;

    private List<PerformanceMonitorNapeVo> children;


}
