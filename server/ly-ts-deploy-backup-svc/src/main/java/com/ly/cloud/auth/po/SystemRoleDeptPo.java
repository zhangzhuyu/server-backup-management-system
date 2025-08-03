package com.ly.cloud.auth.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色和部门关联表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
@TableName("ly_sys_role_dept")
public class SystemRoleDeptPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 7964884528349226152L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "role_id")
    private Long roleId;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "dept_id")
    private Long deptId;

}
