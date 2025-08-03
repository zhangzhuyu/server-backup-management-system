package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 需求反馈信息表
 * @author chenguoqing
 *
 */
@Data
public class DemandFeedbackDto implements Serializable {

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
     * 需求标题
     */
     @ApiModelProperty("需求标题")
    private String title;
    
     /**
     * 紧急程度(枚举)
     */
     @ApiModelProperty("紧急程度(枚举)")
    private String emergency_degree;
    
     /**
     * 需求类型(枚举)
     */
     @ApiModelProperty("需求类型(枚举)")
    private String demand_type;
    
     /**
     * 需求描述
     */
     @ApiModelProperty("需求描述")
    private String description;
    
     /**
     * 附件id
     */
     @ApiModelProperty("附件id")
    private String file_id;
    
     /**
     * 附件名称
     */
     @ApiModelProperty("附件名称")
    private String file_name;
    
     /**
     * 联系人
     */
     @ApiModelProperty("联系人")
    private String contact_name;
    
     /**
     * 联系方式
     */
     @ApiModelProperty("联系方式")
    private String contact_way;
    
     /**
     * 发送状态(枚举)
     */
     @ApiModelProperty("发送状态(枚举)")
    private String send_status;
    
     /**
     * 处理人ID
     */
     @ApiModelProperty("处理人ID")
    private String worker_id;
    
     /**
     * 处理状态(枚举)
     */
     @ApiModelProperty("处理状态(枚举)")
    private String work_status;
    
     /**
     * 处理时间
     */
     @ApiModelProperty("处理时间")
    private java.util.Date work_time;
    
     /**
     * 结果反馈
     */
     @ApiModelProperty("结果反馈")
    private String result_feedback;
    
     /**
     * 操作人ID
     */
     @ApiModelProperty("操作人ID")
    private String operator_id;
    
     /**
     * 操作时间
     */
     @ApiModelProperty("操作时间")
    private java.util.Date 	 operation_time;
    
     /**
     * 备注
     */
     @ApiModelProperty("备注")
    private String remark;
    

}
