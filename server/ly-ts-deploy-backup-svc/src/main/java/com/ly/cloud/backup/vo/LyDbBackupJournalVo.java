package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class LyDbBackupJournalVo implements Serializable {

    private static final long serialVersionUID = -2821042226118038863L;

    @ApiModelProperty("备份日志")
    private String journal;

    @ApiModelProperty("备份状态")
    private String backupStatus;


}
