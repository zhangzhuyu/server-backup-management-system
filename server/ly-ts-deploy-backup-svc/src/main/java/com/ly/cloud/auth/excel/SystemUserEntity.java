package com.ly.cloud.auth.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author wzz
 * @title: SystemUserEntity
 * @date 2023/2/13 14:58
 */
@Data
public class SystemUserEntity {

    @Excel(name = "*序号", isImportField ="true_st", width = 21)
    private Integer userSort;

    @Excel(name = "*姓名", isImportField ="true_st", width = 42)
    private String nickName;

    @Excel(name = "*用户账号", isImportField ="true_st", width = 42)
    private String userName;

    @Excel(name = "*密码", isImportField ="true_st", width = 42)
    private String password;

    @Excel(name = "*所属部门", isImportField ="true_st", width = 42)
    private String deptName;

    @Excel(name = "*性别", isImportField ="true_st", width = 42)
    private String sex;

    @Excel(name = "邮箱", width = 84)
    private String email;

    @Excel(name = "手机号码", width = 63)
    private String phonenumber;

    @Excel(name = "*角色", isImportField ="true_st", width = 42)
    private String roleName;

    @Excel(name = "*状态", isImportField ="true_st", width = 42)
    private String status;

    /**
     * 必填的字段
     * @return
     */
    /*public static List<String> getNotNullColumnNames(){
        Map<String, String> columnNames = SystemUserEntity.getColumnNames();
        Collection<String> values = columnNames.values();
        values.removeIf(i->i.indexOf("*") ==-1);
        List<String> result = new ArrayList<>(values);
        return result;
    }*/

    /**
     * 所有字段
     * @return
     */
    /*public static Map<String,String> getColumnNames(){
        Map<String, String> result = new LinkedHashMap<>();
        String prefix = "用户信息导入导出模板.";
        result.put("waterMeterName",prefix+"*水表名称");
        result.put("waterMeterNo",prefix+"*编号");
        result.put("categoryName",prefix+"*所属分类");
        result.put("placeNum",prefix+"*关联地点编号");
        return result;
    }*/
}
