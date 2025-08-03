package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 告警订阅内容表(WarningSubscriptionContentPo)实体类
 *
 * @author makejava
 * @since 2022-06-14 16:59:45
 */
@TableName(value = "ly_mw_warning_subscription_object")
@Data
public class WarningSubscriptionObjectPo implements Serializable {
    private static final long serialVersionUID = 234902815903221448L;

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
     * 订阅的部门
     */
    @TableField(value = "object_department")
    private String objectDepartment;

    /**
     *  订阅用户
     */
    @TableField(value = "object_user")
    private String objectUser;



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

