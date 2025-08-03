package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务领域代码视图 dto
 * @author SYC
 */
@Data
@TableName("ly_rm_business_domain")
public class BusinessDomainPo implements Serializable {

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
     * 代码项中文名称
     */
    @TableField(value = "code_name")
    private String codeName;

    /**
     * 代码项值
     */
    @TableField(value = "code_key")
    private String codeKey;

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

