package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 聚合日志eChart数据
 * @author wzz
 *
 */
@Data
public class LogAggregationVo implements Serializable {

    /**
     * 日志发生次数
     */
    private int logSum;

    /**
     * 正常日志发生次数
     */
    private int infoLogSum;

    /**
     * 错误日志发生次数
     */
    private int errorLogSum;

    /**
     * 按时间区分日志发生次数
     */
    private List<Map<String, String>> eChartList;

    /**
     * 所有服务名称
     */
    private List<String> serviceNames;
}
