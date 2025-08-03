package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 消息管理信息表：ly_ms_message_management
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_ms_message_management")
public class MessageManagementPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 消息标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 消息描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 消息类型(枚举)
     */
    @TableField(value = "message_type")
    private String message_type;

    /**
     * 紧急程度(枚举)
     */
    @TableField(value = "emergency_degree")
    private String emergency_degree;

    /**
     * 执行状态(枚举)
     */
    @TableField(value = "executing_status")
    private String executing_status;

    /**
     * 执行时间
     */
    @TableField(value = "executing_time")
    private java.util.Date executing_time;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operator_id;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operation_time;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}

