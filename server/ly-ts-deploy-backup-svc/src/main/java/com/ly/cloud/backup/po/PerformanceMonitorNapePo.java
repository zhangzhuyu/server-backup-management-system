package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ly_xn_monitoring_nape")
public class PerformanceMonitorNapePo {

    private static final long serialVersionUID = 7653217737376520724L;


    @TableId(value = "id")
    private Long id;

    @TableField(value = "nape_type")
    private String napeType;

    @TableField(value = "nape_name")
    private String napeName;

    @TableField(value = "parent_nape_type")
    private String parentNapeType;

    @TableField(value = "leaf_node")
    private String leafNode;






}
