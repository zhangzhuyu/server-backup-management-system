package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class LyDbBackupListVo implements Serializable {

    private static final long serialVersionUID = 5291218695650981378L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("备份名称")
    private String title;

    @ApiModelProperty("备份模式")
    private String totalMethod;

    @ApiModelProperty("备份策略id")
    private String strategyId;

    @ApiModelProperty("备份执行时间")
    private Date backupTime;

    @ApiModelProperty("百分比")
    private String proportion;

    @ApiModelProperty("备份状态")
    private String backupStatus;

}
