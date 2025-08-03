package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 巡检记录信息表
 * @author chenguoqing
 *
 */
@Data
public class InspectionRecordDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

     /**
     * 主键
     */
     @ApiModelProperty("主键")
    private Long id;
    
     /**
     * 报告名称
     */
     @ApiModelProperty("报告名称")
    private String name;
    
     /**
     * 报告文件ID
     */
     @ApiModelProperty("报告文件ID")
    private String file_id;
    
     /**
     * 执行方式（枚举）
     */
     @ApiModelProperty("执行方式（枚举）")
    private String executive_mode;
    
     /**
     * 巡检策略ID（外键）
     */
     @ApiModelProperty("巡检策略ID（外键）")
    private String strategy_id;
    
     /**
     * 巡检结果（枚举）
     */
     @ApiModelProperty("巡检结果（枚举）")
    private String inspection_result;
    
     /**
     * 巡检结果反馈
     */
     @ApiModelProperty("巡检结果反馈")
    private String inspection_feedback;
    
     /**
     * 最近巡检开始时间
     */
     @ApiModelProperty("最近巡检开始时间")
    private java.util.Date start_time;
    
     /**
     * 最近巡检结束时间
     */
     @ApiModelProperty("最近巡检结束时间")
    private java.util.Date end_time;
    
     /**
     * 推送结果（枚举）
     */
     @ApiModelProperty("推送结果（枚举）")
    private String push_result;
    
     /**
     * 推送结果反馈
     */
     @ApiModelProperty("推送结果反馈")
    private String push_feedback;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operator_id;
    
     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date operation_time;
    
     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;
    

}
