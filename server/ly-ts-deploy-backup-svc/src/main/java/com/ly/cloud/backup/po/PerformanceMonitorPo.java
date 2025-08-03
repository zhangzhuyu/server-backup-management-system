package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ly_xn_monitoring")
public class PerformanceMonitorPo implements Serializable {

    private static final long serialVersionUID = 7969422290253922556L;

    @TableId(value = "id")
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "nape_type")
    private String napeType;

    @TableField(value = "enable")
    private String enable;

    @TableField(value = "operating_cycle")
    private String operatingCycle;

    @TableField(value = "run_time")
    private String runTime;

    @TableField(value = "target_sourceId")
    private String targetSourceId;

    @TableField(value = "operation_date")
    private String operationDate;

    @TableField("strategic_modeId")
    private String strategicModeId;

}
