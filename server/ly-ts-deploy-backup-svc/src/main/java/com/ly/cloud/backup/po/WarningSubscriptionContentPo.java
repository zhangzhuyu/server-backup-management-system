package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 告警订阅内容表(WarningSubscriptionContentPo)实体类
 *
 * @author makejava
 * @since 2022-06-14 16:59:45
 */
@TableName(value = "ly_mw_warning_subscription_content")
@Data
public class WarningSubscriptionContentPo implements Serializable {
    private static final long serialVersionUID = 174902815903221448L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 订阅ID
     */
    @TableField(value = "subscription_id")
    private String subscriptionId;

    /**
     * 订阅类型
     */
    @TableField(value = "subscribe_type")
    private String subscribeType;

    /**
     * 是否启用
     */
    @TableField(value = "enable")
    private String enable;

    /**
     *  订阅内容
     */
    @TableField(value = "subscribe_content")
    private String subscribeContent;



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

