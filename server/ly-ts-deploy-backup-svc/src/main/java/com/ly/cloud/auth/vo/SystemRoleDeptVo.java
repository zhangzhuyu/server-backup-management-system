package com.ly.cloud.auth.vo;

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
public class SystemRoleDeptVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3695529362478660819L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 部门ID
     */
    private Long deptId;
    
}
