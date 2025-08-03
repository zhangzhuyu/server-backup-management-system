package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @Author ljb
 * @Date 2022/5/23
 */


@Data
@TableName("ly_mw_warning_record_object")
public class WarningRecordObjectPo {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -5113609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 关联告警规则ID（外键）
     */
    @TableField(value = "record_id")
    private String recordId;

    /**
     *
     */
    @TableField(value = "warning_object")
    private String warningObject;

    /**
     *告警接口
     */
    @TableField(value = "warning_interface")
    private String warningInterface;

    /**
     *
     */
    @TableField(value = "object_type")
    private String objectType;

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

    /**
     * 统计数量
     */
    @TableField(exist = false)
    private int count;
}
