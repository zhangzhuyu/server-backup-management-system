package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("资源统计Vo")
@Data
public class OrcalTotalVo {
    //资源类型
    private String resourcesType;
    //资源关联的规则数
    private Long ruleTotal;
    //资源关联规则的记录数
    private Long recordTotal;

}
