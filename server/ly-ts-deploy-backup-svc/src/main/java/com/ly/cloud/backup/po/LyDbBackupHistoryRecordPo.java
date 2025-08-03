package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 备份历史记录表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "ly_db_backup_history_record")
public class LyDbBackupHistoryRecordPo implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 备份策略（枚举）
     */
    @TableField(value = "backup_strategy_type")
    private String backupStrategyType;

    /**
     * 备份时间
     */
    @TableField(value = "backup_time")
    private Date backupTime;

    /**
     * 备份状态（枚举）
     */
    @TableField(value = "backup_status")
    private String backupStatus;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 时间戳（minio上面）
     */
    @TableField(value = "time_stamp")
    private String timeStamp;

    /**
     * 策略表的id
     */
    @TableField(value = "strategy_id")
    private String strategyId;

    /**
     * 进度百分比
     */
    @TableField(value = "proportion")
    private String proportion;

    @TableField(value = "user_name")
    private String userName;

    @TableField(value = "journal")
    private String journal;

    @TableField(value = "data_source_type")
    private String dataSourceType;

    @TableField(value = "backup_way")
    private String backupWay;

    /**
     * 备份方式
     */
    @TableField(value = "total_method")
    private String totalMethod;

    @TableField(value = "url")
    private String url;

    @TableField(value = "backup_method")
    private String backupMethod;

    @TableField(value = "backup_target")
    private String backupTarget;

    @TableField(value = "operating_cycle")
    private String operatingCycle;

    @TableField(value = "run_time")
    private String runTime;

    @TableField(value = "task_mode")
    private String taskMode;

    @TableField(value = "size")
    private String size;

    /**
     * 所属部门编号
     */
    @TableField(value = "auth_dept_id")
    private String authDeptId;

    @TableField(exist = false)
    private List<String> authDeptIds;

    private static final long serialVersionUID = 1L;
}





















