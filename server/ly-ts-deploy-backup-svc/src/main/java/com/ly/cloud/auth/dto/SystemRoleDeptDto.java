package com.ly.cloud.auth.dto;

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
public class SystemRoleDeptDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2887980392028678758L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "用户ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long deptId;
    
}
