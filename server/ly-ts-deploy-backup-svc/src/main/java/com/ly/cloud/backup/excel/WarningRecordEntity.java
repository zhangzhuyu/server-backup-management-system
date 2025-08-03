package com.ly.cloud.backup.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class WarningRecordEntity {
    @Excel(name = "预警规则名称", width = 30)
    private String ruleName;
    @Excel(name = "指标对象", width = 30)
    private String ruleObject;
    @Excel(name = "预警对象", width = 30)
    private String warningObject; //Id
    @Excel(name = "预警等级", width = 30)
    private String warningLevel;
    @Excel(name = "预警指标类型", width = 30)
    private String exceptionType;
    @Excel(name = "预警描述", width = 30)
    private String warningDescription;
    @Excel(name = "最近预警时间", width = 30)
    private String warningTime;
}
