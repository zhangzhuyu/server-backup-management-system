package com.ly.cloud.backup.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 告警规则信息视图类
 *
 * @author jiangzhongxin
 */
@Data
@ApiModel
public class WarningRuleListVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -4969174612975898769L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 指标对象（枚举）
     */
    @ApiModelProperty("指标对象(1-主机 2-数据库 3-服务 4-应用 5-中间件)")
    private String target;

    /**
     * 指标类型中文名字
     */
    @ApiModelProperty("指标类型")
    private String typeName;


    /**
     * 指标类型id
     */
    private String type;

    /**
     *  监控规则名称
     */
    @ApiModelProperty("指标名称")
    private String name;

    /**
     * 阈值范围1（枚举）
     */
    @ApiModelProperty("阈值操作符")
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

    private java.lang.String runningTime;

    /**
     * 运行时间单位
     */
    private java.lang.String runningTimeUnit;

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
     * 告警等级
     */
    @ApiModelProperty("告警等级(1-一般 2-警告 3-严重 4紧急)")
    private String warningLevel;

    /**
     * 告警等级 (枚举)
     */
    private String alarmLevel;

    /**
     * 预警对象
     */
    @ApiModelProperty("预警对象")
    private String warningObject;

    /**
     * 其他条件
     */
    private String param0 ;
    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用(1-开启，0-禁用)")
    private String enable;

    /**
     * 调度间隔
     */
    @ApiModelProperty("调度间隔值")
    private java.lang.String gapValue;

    /**
     * 调度间隔单位
     */
    @ApiModelProperty("调度间隔单位")
    private java.lang.String gapValueUnit;

    /**
     * 是否需要生成工单，1要，0不要，
     */
    @ApiModelProperty("是否需要生成工单，1要，0不要")
    private java.lang.String enableWorkOrder;

    /**
     * 操作符类型
     */
    private String operatorType;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 预警主机集合
     */
    private List<String> earlyWarningHostList;

    /**
     * 延迟操作符(接口延迟与吞吐量专用字段)
     */
    @ApiModelProperty("延迟操作符(接口延迟与吞吐量专用字段)")
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
    @ApiModelProperty("吞吐量值(接口延迟与吞吐量专用字段)")
    private String throughput;

    /**
     * 吞吐单位
     */
    @ApiModelProperty("吞吐量单位(接口延迟与吞吐量专用字段)")
    private String throughputUnit;

    /**
     * 延迟与吞吐量条件关系符
     */
    @ApiModelProperty("延迟与吞吐量条件关系符(&&-并且，||-或者,接口延迟与吞吐量专用字段)")
    private String delayThroughputRelator;

    /**
     * 推送模式(1-一次性,2-连续性,3-工作日,4-自定义)
     */
    @ApiModelProperty("推送模式(1-一次性,2-连续性,3-工作日,4-自定义)")
    private String pushMode;

    /**
     * 间隔时间值（连续性推送方式专用字段）
     */
    @ApiModelProperty("间隔时间值（连续性推送方式专用字段）")
    private Integer intervalValue;

    /**
     * 间隔时间单位(连续性推送方式专用字段)
     */
    @ApiModelProperty("间隔时间单位(连续性推送方式专用字段)")
    private String intervalUnit;

    /**
     * 工作日(1-周一,2-周二,3-周三,4-周四,5-周五,6-周六,7-周日 工作日之间用逗号隔开)
     */
    @ApiModelProperty("工作日(1-周一,2-周二,3-周三,4-周四,5-周五,6-周六,7-周日 工作日之间用逗号隔开,推送相关字段)")
    private String weekday;

    /**
     * 开始日期(只有自定义模式才有开始日期)
     */
    @ApiModelProperty("开始日期(只有自定义模式才有开始日期，推送相关字段)")
    private String startDate;

    /**
     * 结束日期(只有自定义模式才有结束日期)
     */
    @ApiModelProperty("结束日期(只有自定义模式才有结束日期，推送相关字段)")
    private String endDate;

    /**
     * 开始时间(时，分，秒)
     */
    @ApiModelProperty("开始时间(时，分，秒，推送相关字段)")
    private String startTime;

    /**
     * 结束时间(时，分，秒)
     */
    @ApiModelProperty("结束时间(时，分，秒，推送相关字段)")
    private String endtime;



}
