package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 告警订阅信息表
 *
 * @author admin
 * @TableName ly_mw_warning_subscription
 */
@TableName(value = "ly_mw_warning_subscription")
@Data
public class WarningSubscriptionPo implements Serializable {

    private static final long serialVersionUID = 7173920828097364308L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 是否启用关系
     */
    @TableField(value = "enable")
    private String enable;

    /**
     * 订阅标题
     */
    @TableField(value = "title")
    private String subscriptionTitle;


    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}