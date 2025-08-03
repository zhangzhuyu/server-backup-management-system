package com.ly.cloud.backup.vo;

import java.io.Serializable;

public class DescribeVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -5190609292510835903L;

    /**
     * 指标类型名称
     */
    private String targetName;

    /**
     * 备注
     */
    private String remark;

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
