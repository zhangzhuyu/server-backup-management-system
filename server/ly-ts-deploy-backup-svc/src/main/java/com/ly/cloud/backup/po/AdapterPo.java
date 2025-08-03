package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库适配器信息：ly_rm_adapter
 *
 * @author chenguoqing
 */
@ApiModel("xyz")
@Data
@TableName("ly_rm_adapter")
public class AdapterPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(value = "")
    private Long id;

    /**
     * 适配器名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 资源包名称
     */
    @TableField(value = "resource_name")
    private String resourceName;

    /**
     * 资源包版本
     */
    @TableField(value = "resource_verson")
    private String resourceVerson;

    /**
     * 资源包兼容版本
     */
    @TableField(value = "compatible_verson")
    private String compatibleVerson;

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

