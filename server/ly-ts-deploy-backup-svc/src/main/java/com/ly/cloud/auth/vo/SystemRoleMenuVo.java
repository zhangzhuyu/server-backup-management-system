package com.ly.cloud.auth.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色和菜单关联表
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Data
public class SystemRoleMenuVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -2249214993720385231L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 角色ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roleId;

    /**
     * 菜单ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long menuId;


}
