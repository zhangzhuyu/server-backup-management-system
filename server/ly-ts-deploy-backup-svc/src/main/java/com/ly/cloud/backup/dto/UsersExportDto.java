package com.ly.cloud.backup.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author wuzuzhong
 */
@Data
public class UsersExportDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -5994767277967346332L;

    /**
     * 用户ID集合
     */
    @ApiModelProperty("用户ID集合")
    private List<Long> userIds;

    /**
     * 所在部门ID
     */
    @ApiModelProperty("所在部门ID")
    private Long deptId;


}
