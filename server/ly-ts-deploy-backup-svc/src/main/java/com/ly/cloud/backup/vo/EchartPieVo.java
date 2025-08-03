package com.ly.cloud.backup.vo;

import lombok.Data;

/**
 * @author SYC
 * @Date: 2023/1/12 10:59
 * @Description 通用的echart图表视图返回对象（饼图）
 */
@Data
public class EchartPieVo {
    /**
     * 名称
     */
    private String name;

    /**
     * 数值
     */
    private Integer value;


    /**
     * 百分比
     */
    private String rate;
}
