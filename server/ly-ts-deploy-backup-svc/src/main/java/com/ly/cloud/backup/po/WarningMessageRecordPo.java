package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 消息发送记录表：ly_mw_warning_message_record
 * @author SYC
 */
@Data
@TableName("ly_mw_warning_message_record")
public class WarningMessageRecordPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 发送方式（钉钉:1、邮件:2、短信:3、企业微信:0）
     */
    @TableField(value = "message_type")
    private int message_type;

    /**
     * 发送状态（1：成功，0：失败）
     */
    @TableField(value = "message_status")
    private int messageStatus;

    /**
     * 发送时间
     */
    @TableField(value = "message_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date messageTime;

    /**
     * 收件人ID
     */
    @TableField(value = "recipients_id")
    private String recipientsId;


    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;



    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;




}

