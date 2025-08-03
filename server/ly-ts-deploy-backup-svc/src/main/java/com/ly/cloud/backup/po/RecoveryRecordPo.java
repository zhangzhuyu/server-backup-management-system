package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 自愈记录信息表：ly_fr_recovery_record
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_fr_recovery_record")
public class RecoveryRecordPo implements Serializable {

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
     * 自愈指标ID（外键）
     */
    @TableField(value = "recovery_id")
    private String recovery_id;

    /**
     * 服务名称ID（外键）
     */
    @TableField(value = "service_id")
    private String service_id;

    /**
     * 自愈开始时间
     */
    @TableField(value = "start_time")
    private java.util.Date start_time;

    /**
     * 自愈结束时间
     */
    @TableField(value = "end_time")
    private java.util.Date end_time;

    /**
     * 自愈尝试次数
     */
    @TableField(value = "recovery_count")
    private Integer recovery_count;

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

