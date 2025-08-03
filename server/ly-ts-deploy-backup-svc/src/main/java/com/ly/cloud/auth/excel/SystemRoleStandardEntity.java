package com.ly.cloud.auth.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 巡检标准
 * @author SAD
 * @since 3021-11-18 20:15
 **/
@Data
public class SystemRoleStandardEntity implements Serializable {
    @Excel(name = "角色ID", width = 30)
    private Long roleId;
    @Excel(name = "角色名称", isImportField ="true_st", width = 30)
    private String roleName;
    @Excel(name = "角色权限字符串", isImportField ="true_st", width = 30)
    private String roleKey;
    @Excel(name = "显示顺序", isImportField ="true_st", width = 20)
    private Integer roleSort;
    @Excel(name = "数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）", isImportField ="true_st", replace = {"全部数据权限_1", "自定数据权限_2", "本部门数据权限_3", "本部门及以下数据权限_4"}, width = 30)
    private String dataScope;
    @Excel(name = "菜单树选择项是否关联显示", isImportField ="true_st", width = 15)
    private Boolean menuCheckStrictly;
    @Excel(name = "部门树选择项是否关联显示", isImportField ="true_st", width = 15)
    private Boolean deptCheckStrictly;
    @Excel(name = "角色状态（0正常 1停用）", isImportField ="true_st", replace = {"正常_0", "停用_1"}, width = 15)
    private String status;
    @Excel(name = "删除标志（0代表存在 2代表删除）", isImportField ="true_st", replace = {"存在_0", "删除_1"}, width = 15)
    private String delFlag;
    @Excel(name = "创建者", isImportField ="true_st", width = 15)
    private String createBy;
    @Excel(name = "创建时间", isImportField ="true_st", format = "yyyy-MM-dd HH:mm:ss", width = 30)
    private Date createTime;
    @Excel(name = "更新者", isImportField ="true_st", width = 20)
    private String updateBy;
    @Excel(name = "更新时间", isImportField ="true_st", format = "yyyy-MM-dd HH:mm:ss", width = 30)
    private Date updateTime;
    @Excel(name = "备注", isImportField ="true_st", width = 30)
    private String remark;
}
