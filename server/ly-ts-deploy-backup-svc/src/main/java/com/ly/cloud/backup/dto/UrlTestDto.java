package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class UrlTestDto implements Serializable {

    @ApiModelProperty(value = "url(主键)")
    private List<String> urlList;
    @ApiModelProperty(value = "数据源类型（枚举）（1为Oracle、2为mysql、6为mongo），选择服务器备份时不需要此参数")
    private Integer sourceType;
    @ApiModelProperty(value = "备份方式（枚举）（1为数据库备份、2为服务器工作目录备份、3为核心表备份）")
    private Integer backupWay;
    @ApiModelProperty(value = "备份方法（枚举）（1为http、2为ssh、3为ftp、4为cifs），选择数据库备份时不需要此参数")
    private Integer backupMethod;
    @ApiModelProperty(value = "备份路径")
    private List<String> backupTarget;
}
