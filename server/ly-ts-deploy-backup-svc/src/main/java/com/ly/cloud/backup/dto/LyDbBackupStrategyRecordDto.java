package com.ly.cloud.backup.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class LyDbBackupStrategyRecordDto implements Serializable {
    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;
    /**
     * 数据源类型（枚举）
     */
    /*@ApiModelProperty(value = "数据源类型集合")
    private List<String> dataSourceType;*/

    @ApiModelProperty(value = "数据源类型")
    private String dataSourceType;

    /**
     * 备份方式（枚举）
     */
    /*@ApiModelProperty(value = "备份方式集合")
    private List<String> backupWay;*/

    @ApiModelProperty(value = "备份方式")
    private String backupWay;

    /**
     * 任务执行模式（枚举）
     */
    @ApiModelProperty("任务执行模式（1为定时、2为间隔）")
    private String taskMode;

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

    /**
     * 备注
     */
    @ApiModelProperty("执行时间（例如：23:00）")
    private String remark;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页")
    private String pageNum;

    /**
     * 一页的内容
     */
    @ApiModelProperty(value = "一页的内容")
    private String pageSize;

    private static final long serialVersionUID = -9003609292510835997L;


    @ApiModelProperty("执行周期（1：每日，2：每周，3：每月）")
    private String operatingCycle;


    @ApiModelProperty("执行日期（1-7 ，1-31）")
    private String operationDate;


    @ApiModelProperty("执行时间（例如：23:00）")
    private String runTime;

    @ApiModelProperty("taier任务的id")
    private String taierId;

    @ApiModelProperty("是否为expdb")
    private String expdb;

    @ApiModelProperty("全部方法")
    private String totalMethod;

    @ApiModelProperty("首次是否立即执行（0为否，1为是）")
    private String isFirstBackup;


    //taier
    @ApiModelProperty("taier任务名")
    private String name;
    @ApiModelProperty("taier任务描述")
    private String taskDesc;
    //保存任务需要的参数
    @ApiModelProperty("taier作业速率上限")
    private Integer speed;
    @ApiModelProperty("taier作业写入并发数（1-20）")
    private Integer writerChannel;
    @ApiModelProperty("taier数据源类型（1为mysql，2为orcle）")
    private Integer type;
    @ApiModelProperty("taier表名")
    private String[] table;
    @ApiModelProperty("taier数据源id")
    private Integer sourceId;
    @ApiModelProperty("taier数据操作")
    private Integer[] cat;
    @ApiModelProperty("taier嵌套json平铺")
    private Boolean pavingData;
    @ApiModelProperty("taier选择目标数据源")
    private Integer sourceIdToTarget;
    @ApiModelProperty("taierkafka消息队列的topic")
    private String topic;
    @ApiModelProperty("taier采集起点（按时间选择）")
    private Long timestamp;
    @ApiModelProperty("taier数据有序")
    private Boolean dataSequence;
    @ApiModelProperty("taier的schema")
    private String schema;

    /**
     * 所属部门编号
     */
    private String authDeptId;

    /**
     * 所属部门编号
     */
    private List<String> authDeptIds;
}





































