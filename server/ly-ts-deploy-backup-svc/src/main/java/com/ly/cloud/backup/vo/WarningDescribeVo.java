package com.ly.cloud.backup.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 预警描述视图类
 */
public class WarningDescribeVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -4190609292510835903L;

    /**
     * 指标类型名称
     */
    private String type;


    private List<DescribeVo> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DescribeVo> getData() {
        return data;
    }

    public void setData(List<DescribeVo> data) {
        this.data = data;
    }
}
