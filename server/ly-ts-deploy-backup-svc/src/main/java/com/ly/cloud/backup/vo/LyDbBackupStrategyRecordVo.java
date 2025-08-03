package com.ly.cloud.backup.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class LyDbBackupStrategyRecordVo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835889L;
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 数据源类型（枚举）
     */
    /*//@TableField(value = "data_source_type")
    private String dataSourceType;*/

    @ApiModelProperty("数据源类型（1为Oracle、2为mysql、6为mongo）")
    private String dataSourceType;

    /**
     * 备份方式（枚举）
     */
    //@TableField(value = "backup_way")
    //private String backupWay;

    @ApiModelProperty("备份方式（1为数据库备份、2为服务器工作目录备份、3为核心表备份）")
    private String backupWay;

    /**
     * 备份方法
     */
    @ApiModelProperty("备份方法（1为http、2为ssh、3为ftp、4为cifs）")
    private String backupMethod;

    /**
     * 备份目标
     */
    @ApiModelProperty("备份目标（服务器工作目录备份时使用，选择要备份文件所在路径）")
    private List<String> backupTarget;

    /**
     * 数据库连接
     */
    @ApiModelProperty("数据库url连接")
    private List<String> url;

    /**
     * 任务执行模式（枚举）
     */
    @ApiModelProperty("任务执行模式（1为定时、2为间隔）")
    private String taskMode;

    /**
     * 是否启用（枚举）
     */
    @ApiModelProperty("是否启用（1为启用，2为停止）")
    private String enable;

    /**
     * 备份时间
     */
    @ApiModelProperty("备份时间")
    private Date backupTime;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间（更新操作）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operationTime;

    @ApiModelProperty("执行周期")
    private String operatingCycle;

    /**
     * cron表达式
     */
    @ApiModelProperty("cron表达式")
    private String cron;

    /**
     * 选择数据库
     */
    @ApiModelProperty("选择数据库（一个数据源有很多数据库，不可以多选）")
    private String databases;

    /**
     * 选择表格
     */
    @ApiModelProperty("选择表格（一个数据库有很多表格，可以多选）")
    private List<String> tables;

    /**
     * 登录地址
     */
    @ApiModelProperty("登录地址（备份方式为服务器工作目录备份时）")
    private String loginUrl;

    /**
     * 登录用户名
     */
    @ApiModelProperty("登录用户名（备份方式为服务器工作目录备份时）")
    private String loginUsername;

    /**
     * 登录密码
     */
    @ApiModelProperty("登录密码（备份方式为服务器工作目录备份时）")
    private String loginPassword;


    @ApiModelProperty("执行周期（1：每日，2：每周，3：每月）")
    private String runningCycle;


    @ApiModelProperty("执行日期（1-7 ，1-31）")
    private String operationDate;


    @ApiModelProperty("执行时间（例如：23:00）")
    private String runTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("taier任务的id")
    private String taierId;

    @ApiModelProperty("是否为expdb")
    private String expdb;

    @ApiModelProperty("全部方法")
    private String totalMethod;

    @ApiModelProperty("最新百分比")
    private String lastProportion;

    @ApiModelProperty("最新备份状态")
    private String lastStatus;

    /**
     * 所属部门编号
     */
    private String authDeptId;

    /**
     * 所属部门编号
     */
    private List<String> authDeptIds;
}

































