package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警记录信息表
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_mw_warning_subscription_method")
public class WarningSubMethodPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -8013609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     *
     */
    @TableField(value = "level_id")
    private String levelId;

    /**
     *
     */
    @TableField(value = "level_name")
    private String levelName;

    /**
     *
     */
    @TableField(value = "enable_ding")
    private String enableDing;

    /**
     *
     */
    @TableField(value = "enable_email")
    private String enableEmail;

    /**
     *
     */
    @TableField(value = "enable_note")
    private String enableNote;



    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


}
