package com.ly.cloud.backup.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 告警规则信息表
 *
 * @author chenguoqing
 */
@Data
@ApiModel
public class WarningRuleDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 9105376222586390168L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 指标对象（枚举）
     */
    @ApiModelProperty("指标对象（枚举）")
    private String target;

    @ApiModelProperty("指标对象列表(用于多选)")
    private List<String> targetList;

    /**
     * 指标类型（枚举）
     */
    @ApiModelProperty("指标类型（枚举）")
    private String type;

    /**
     * 指标名称
     */
    @ApiModelProperty("指标名称")
    private String name;

    /**
     * 阈值范围1（枚举）
     */
    @ApiModelProperty("阈值操作符1")
    private String thresholdOperators1;

    /**
     * 阈值1
     */
    @ApiModelProperty("阈值")
    private String threshold1;

    /**
     * 阈值单位
     */
    @ApiModelProperty("阈值单位")
    private String thresholdUnit1;

    /**
     * 运行时间
     */
    @ApiModelProperty("运行时间")
    private String runningTime;

    /**
     * 运行时间单位（枚举）
     */
    @ApiModelProperty("运行时间单位（枚举）")
    private String runningTimeUnit;

    /**
     * 重复次数操作符
     */
    @ApiModelProperty("重复次数操作符")
    private java.lang.String repeatOperators;

    /**
     * 重复次数
     */
    @ApiModelProperty("重复次数")
    private java.lang.String repeatValue;

    /**
     * 告警等级（枚举）
     */
    @ApiModelProperty("告警等级（枚举）")
    private String warningLevel;

    @ApiModelProperty("告警等级(用于多选，等级之间使用逗号隔开)")
    private List<String> warningLevelList;

    /**
     * 预警对象
     */
    @ApiModelProperty("预警对象")
    private String warningObject;

    /**
     * 其他条件
     */
    @ApiModelProperty("其他参数0")
    private String param0 ;


    /**
     * 预警对象值数组
     */
    @ApiModelProperty("预警对象值数组")
    private List<String> earlyWarningHostList = new ArrayList<>();

    /**
     * 是否立即启用 1 启用 0 不启用
     */
    @ApiModelProperty("是否立即启用 1 启用 0 不启用")
    private String enable;

    /**
     * 调度间隔
     */
    @ApiModelProperty("调度间隔")
    private java.lang.String gapValue;

    /**
     * 调度间隔单位
     */
    @ApiModelProperty("调度间隔单位")
    private String gapValueUnit;

    /**
     * 是否需要生成工单，true要，false不要，
     */
    @ApiModelProperty("是否需要生成工单，1要，0不要，")
    private String enableWorkOrder;

    /**
     * 是否需要生成工单，true要，false不要，
     */
    @ApiModelProperty("搜索内容")
    private String content;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String operatorId;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 吞吐量操作符
     */
    @ApiModelProperty("延迟值操作符(接口延迟与吞吐量专用字段)")
    private String delayOperators;

    /**
     * 延迟值
     */
    @ApiModelProperty("延迟值(接口延迟与吞吐量专用字段)")
    private String delay;

    /**
     * 延迟单位
     */
    @ApiModelProperty("延迟单位(接口延迟与吞吐量专用字段)")
    private String delayUnit;


    /**
     *吞吐量操作符
     */
    @ApiModelProperty("吞吐量操作符(接口延迟与吞吐量专用字段)")
    private String throughputOperators;

    /**
     * 吞吐值
     */
    @ApiModelProperty("吞吐值(接口延迟与吞吐量专用字段)")
    private String throughput;

    /**
     * 吞吐单位
     */
    @ApiModelProperty("吞吐单位(接口延迟与吞吐量专用字段)")
    private String throughputUnit;


    /**
     * 延迟与吞吐量条件关系符
     */
    @ApiModelProperty("延迟与吞吐量条件关系符(&&-并且，||-或者,接口延迟与吞吐量专用字段)")
    private String delayThroughputRelator;

    @ApiModelProperty("推送模式(1-一次性,2-连续性,3-工作日,4-自定义)")
    private String pushMode;

    @ApiModelProperty("间隔时间值（连续性推送方式专用字段）")
    private Integer intervalValue;

    @ApiModelProperty("间隔时间单位(连续性推送方式专用字段)")
    private String intervalUnit;

    @ApiModelProperty("工作日(1-周一,2-周二,3-周三,4-周四,5-周五,6-周六,7-周日 工作日之间用逗号隔开)")
    private String weekday;

    @ApiModelProperty("开始日期(只有自定义模式才有开始日期)")
    private String startDate;

    @ApiModelProperty("结束日期(只有自定义模式才有结束日期)")
    private String endDate;

    @ApiModelProperty("开始时间(时，分，秒)")
    private String startTime;

    @ApiModelProperty("结束时间(时，分，秒)")
    private String endtime;




    public void setDto(WarningRuleDto dto){
        this.id = dto.id;
        this.target = dto.target;
        this.type = dto.type;
        this.name = dto.name;
        this.threshold1 = dto.threshold1;
        this.thresholdOperators1 = dto.thresholdOperators1;
        this.thresholdUnit1 = dto.thresholdUnit1;
        this.runningTime = dto.runningTime;
        this.runningTimeUnit = dto.runningTimeUnit;
        this.repeatOperators = dto.repeatOperators;
        this.repeatValue = dto.repeatValue;
        this.earlyWarningHostList = dto.earlyWarningHostList;
        this.gapValue = dto.gapValue;
        this.gapValueUnit = dto.gapValueUnit;
    }

}
