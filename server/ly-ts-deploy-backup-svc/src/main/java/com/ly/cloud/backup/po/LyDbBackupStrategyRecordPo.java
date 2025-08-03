package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 备份策略记录表
 * @author zhangzhuyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("ly_db_backup_strategy_record")
@TableName("ly_db_backup_strategy_record")
public class LyDbBackupStrategyRecordPo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835886L;
    /**
     * 主键
     */
    @ApiModelProperty("主键")
//    @TableId(value = "id", type = IdType.AUTO)
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    @TableField(value = "title",strategy = FieldStrategy.IGNORED)
    private String title;

    /**
     * 数据源类型（枚举）
     */
    /*//@TableField(value = "data_source_type")
    private String dataSourceType;*/

    @ApiModelProperty("数据源类型（1为Oracle、2为mysql、6为mongo）")
    @TableField(value = "data_source_type",strategy = FieldStrategy.IGNORED)
    private String dataSourceType;

    /**
     * 备份方式（枚举）
     */
    //@TableField(value = "backup_way")
    //private String backupWay;

    @ApiModelProperty("备份方式（1为数据库备份、2为服务器工作目录备份、3为核心表备份）")
    @TableField(value = "backup_way",strategy = FieldStrategy.IGNORED)
    private String backupWay;

    /**
     * 备份方法
     */
    @ApiModelProperty("备份方法（1为http、2为ssh、3为ftp、4为cifs）")
    @TableField(value = "backup_method",strategy = FieldStrategy.IGNORED)
    private String backupMethod;

    /**
     * 备份目标
     */
    @ApiModelProperty("备份目标（服务器工作目录备份时使用，选择要备份文件所在路径）")
    @TableField(value = "backup_target",strategy = FieldStrategy.IGNORED)
    private String backupTarget;

    /**
     * 数据库连接
     */
    @ApiModelProperty("数据库url连接")
    @TableField(value = "url",strategy = FieldStrategy.IGNORED)
    private String url;

    /**
     * 任务执行模式（枚举）
     */
    @ApiModelProperty("任务执行模式（1为定时、2为间隔）")
    @TableField(value = "task_mode",strategy = FieldStrategy.IGNORED)
    private String taskMode;

    /**
     * 是否启用（枚举）
     */
    @ApiModelProperty("是否启用（1为启用，2为停止）")
    @TableField(value = "enable",strategy = FieldStrategy.IGNORED)
    private String enable;

    /**
     * 备份时间
     */
    @ApiModelProperty("备份时间")
    @TableField(value = "backup_time",strategy = FieldStrategy.IGNORED)
    private Date backupTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    @TableField(value = "operator_id",strategy = FieldStrategy.IGNORED)
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间（更新操作）")
    @TableField(value = "operation_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operationTime;

    @ApiModelProperty("执行周期")
    @TableField(value = "operating_cycle",strategy = FieldStrategy.IGNORED)
    private String operatingCycle;

    /**
     * cron表达式
     */
    @ApiModelProperty("cron表达式")
    @TableField(value = "cron",strategy = FieldStrategy.IGNORED)
    private String cron;

    /**
     * 选择数据库
     */
    @ApiModelProperty("选择数据库（一个数据源有很多数据库，不可以多选）")
    @TableField(value = "`databases`",strategy = FieldStrategy.IGNORED)
    private String databases;

    /**
     * 选择表格
     */
    @ApiModelProperty("选择表格（一个数据库有很多表格，可以多选）")
    @TableField(value = "`tables`",strategy = FieldStrategy.IGNORED)
    private String tables;

    /**
     * 登录地址
     */
    @ApiModelProperty("登录地址（备份方式为服务器工作目录备份时）")
    @TableField(value = "login_url",strategy = FieldStrategy.IGNORED)
    private String loginUrl;

    /**
     * 登录用户名
     */
    @ApiModelProperty("登录用户名（备份方式为服务器工作目录备份时）")
    @TableField(value = "login_username",strategy = FieldStrategy.IGNORED)
    private String loginUsername;

    /**
     * 登录密码
     */
    @ApiModelProperty("登录密码（备份方式为服务器工作目录备份时）")
    @TableField(value = "login_password",strategy = FieldStrategy.IGNORED)
    private String loginPassword;

/*
    @ApiModelProperty("执行周期（1：每日，2：每周，3：每月）")
    @TableField(value = "operating_cycle",strategy = FieldStrategy.IGNORED)
    private String operatingCycle;
//    private String runningCycle;*/


    @ApiModelProperty("执行日期（1-7 ，1-31）")
    @TableField(value = "operation_date",strategy = FieldStrategy.IGNORED)
    private String operationDate;


    @ApiModelProperty("执行时间（例如：23:00）")
    @TableField(value = "run_time",strategy = FieldStrategy.IGNORED)
    private String runTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField(value = "remark",strategy = FieldStrategy.IGNORED)
    private String remark;

    @ApiModelProperty("taier任务的id")
    @TableField(value = "taier_id",strategy = FieldStrategy.IGNORED)
    private String taierId;

    @ApiModelProperty("是否为expdb")
    @TableField(value = "expdb",strategy = FieldStrategy.IGNORED)
    private String expdb;

    @ApiModelProperty("全部方法")
    @TableField(value = "total_method",strategy = FieldStrategy.IGNORED)
    private String totalMethod;

    /*@ApiModelProperty("首次是否立即执行（0为否，1为是）")
    @TableField(value = "is_first_backup",strategy = FieldStrategy.IGNORED)
    private String isFirstBackup;*/

    /**
     * 所属部门编号
     */
    @TableField(value = "auth_dept_id")
    private String authDeptId;

    @TableField(exist = false)
    private List<String> authDeptIds;
}

































