package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class LyDbBackupLevitatedSphereVo implements Serializable {

    private static final long serialVersionUID = -6306204398816088505L;

    @ApiModelProperty("当天结束备份百分比")
    private Double proportion;

    @ApiModelProperty("当天备份任务总数")
    private Integer total;

    @ApiModelProperty("当天备份结束任务总数")
    private Integer endTotal;

    @ApiModelProperty("当天备份状态")
    private String backupStatus;

    @ApiModelProperty("当天备份成功数量")
    private Integer successTotal;

    @ApiModelProperty("当天备份失败数量")
    private Integer failTotal;

    @ApiModelProperty("当天备份停止数量")
    private Integer stopTotal;

    @ApiModelProperty("当天备份正在备份数量")
    private Integer processTotal;

    @ApiModelProperty("当天备份列表")
    private List<LyDbBackupListVo> backupList;

    @ApiModelProperty("备份总表数")
    private Integer numberOfTables;

    @ApiModelProperty("总字段数")
    private Integer numberOfFields;

    @ApiModelProperty("数据容量")
    private Integer dataCapacity;


}
