package com.ly.cloud.backup.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 消息管理信息表
 * @author chenguoqing
 *
 */
@Data
public class MessageManagementDto implements Serializable {

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
     * 消息标题
     */
     @ApiModelProperty("消息标题")
    private String title;
    
     /**
     * 消息描述
     */
     @ApiModelProperty("消息描述")
    private String description;
    
     /**
     * 消息类型(枚举)
     */
     @ApiModelProperty("消息类型(枚举)")
    private String message_type;
    
     /**
     * 紧急程度(枚举)
     */
     @ApiModelProperty("紧急程度(枚举)")
    private String emergency_degree;
    
     /**
     * 执行状态(枚举)
     */
     @ApiModelProperty("执行状态(枚举)")
    private String executing_status;
    
     /**
     * 执行时间
     */
     @ApiModelProperty("执行时间")
    private java.util.Date executing_time;
    
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

    /**
     * 分配服务
     */
    @ApiModelProperty("分配服务")
    private String allotService;
    

}
