package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class LyDbBackupHistoryDetailsVo implements Serializable {

    private static final long serialVersionUID = -2821042226518039711L;

    @ApiModelProperty("备份进度百分比")
    private String proportion;

    @ApiModelProperty("策略名称")
    private String title;

    @ApiModelProperty("类型")
    private String backupWay;

    @ApiModelProperty("数据源类型")
    private String dataSourceType;

    @ApiModelProperty("数据库连接")
    private List<String> url;


    @ApiModelProperty("方式")
    private String totalMethod;

    @ApiModelProperty("执行模式")
    private String taskMode;


    @ApiModelProperty("执行周期")
    private String operatingCycle;

    @ApiModelProperty("执行时间")
    private String runTime;

    @ApiModelProperty("执行用户Id")
    private String operatorName;

    @ApiModelProperty("大小")
    private String size;

    @ApiModelProperty("备份文件")
    private String timeStamp;

    @ApiModelProperty("备份时间(实际备份开始时间)")
    private Date backupTime;

    @ApiModelProperty("备份时间(备份结束时间)")
    private Date operationTime;

    @ApiModelProperty("备份状态")
    private String backupStatus;


    @ApiModelProperty("备份方法（1为http、2为ssh、3为ftp、4为cifs）")
    private List<String> backupTarget;

    @ApiModelProperty("备份目标（服务器工作目录备份时使用，选择要备份文件所在路径）")
    private String backupMethod;

    @ApiModelProperty("备份策略（枚举）")
    private String backupStrategyType;

    /**
     * 所属部门编号
     */
    private String authDeptId;

    /**
     * 所属部门编号
     */
    private List<String> authDeptIds;

}
