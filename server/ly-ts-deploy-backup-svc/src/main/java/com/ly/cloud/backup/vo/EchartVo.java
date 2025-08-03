package com.ly.cloud.backup.vo;

import lombok.Data;

import java.util.List;

/**
 * @author SYC
 * @Date: 2023/1/12 10:59
 * @Description 通用的echart图表视图返回对象（折线图）
 */
@Data
public class EchartVo {
    /**
     * x轴（一般为时间线）
     */
    private List<String> xAxis;

    /**
     * y轴（一般为纯数字）
     */
    private List<Integer> yAxis;


    /**
     * y轴（string类型）
     */
    private List<String> yAxiStrings;

    /**
     * 阈值
     */
    private String threshold;
}
