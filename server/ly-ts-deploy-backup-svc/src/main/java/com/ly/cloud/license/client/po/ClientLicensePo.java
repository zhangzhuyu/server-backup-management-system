package com.ly.cloud.license.client.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 自定义需要校验的License参数
 * @author SYC
 */
@Data
@TableName("ly_sm_system_license")
public class ClientLicensePo implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;

    /**
     * id
     */
    @TableId("id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 项目地名称
     */
    @TableField(value = "school_name")
    private String schoolName;

    /**
     * 联系人
     */
    @TableField(value = "contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

    /**
     * 开始时间
     */
    @TableField(value = "start_time", fill = FieldFill.INSERT, update = "sysdate()")
//    @TableField(value = "start_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time", fill = FieldFill.INSERT, update = "sysdate()")
    private java.util.Date endTime;

    /**
     * 有效期：1:1周、2:1个月 3:3个月 4:半年 5:1年 -1:永久（选择）
     */
    @TableField(value = "expiry_date")
    private int expiryDate;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 描述
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 审批状态(1:申请，2：已处理)
     */
    @TableField(value = "status")
    private int status;

    /**
     * 平台版本号
     */
    @TableField(value = "system_version")
    private String systemVersion;

    /**
     * license过期提醒天数
     */
    @TableField(value = "license_expiry_date")
    private int licenseExpiryDate;

}
