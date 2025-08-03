package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 需求反馈信息表：ly_ms_demand_feedback
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_ms_demand_feedback")
public class DemandFeedbackPo implements Serializable {

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
     * 需求标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 紧急程度(枚举)
     */
    @TableField(value = "emergency_degree")
    private String emergency_degree;

    /**
     * 需求类型(枚举)
     */
    @TableField(value = "demand_type")
    private String demand_type;

    /**
     * 需求描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 附件id
     */
    @TableField(value = "file_id")
    private String file_id;

    /**
     * 附件名称
     */
    @TableField(value = "file_name")
    private String file_name;

    /**
     * 联系人
     */
    @TableField(value = "contact_name")
    private String contact_name;

    /**
     * 联系方式
     */
    @TableField(value = "contact_way")
    private String contact_way;

    /**
     * 发送状态(枚举)
     */
    @TableField(value = "send_status")
    private String send_status;

    /**
     * 处理人ID
     */
    @TableField(value = "worker_id")
    private String worker_id;

    /**
     * 处理状态(枚举)
     */
    @TableField(value = "work_status")
    private String work_status;

    /**
     * 处理时间
     */
    @TableField(value = "work_time")
    private java.util.Date work_time;

    /**
     * 结果反馈
     */
    @TableField(value = "result_feedback")
    private String result_feedback;

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

