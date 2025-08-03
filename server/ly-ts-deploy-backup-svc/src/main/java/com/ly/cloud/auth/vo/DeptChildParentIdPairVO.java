package com.ly.cloud.auth.vo;

import lombok.Data;

/**
 * <p>
 * 部门父子级关系表
 * </p>
 *
 * @author wuzuzhong
 * @since 2023-01-10
 */
@Data
public class DeptChildParentIdPairVO {
    /**
     * 主键 部门ID
     */
    private Long deptId;

    /**
     * 上级部门ID
     */
    private Long parentDeptId;
}
