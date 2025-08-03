package com.ly.cloud.backup.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库备份类型公共枚举类
 *
 * @author: zhangzhuyu
 */
public enum BackupWayEnums {

    /**
     * 备份类型
     */
    DATABASE_BACKUP(1, "数据库备份"),
    DATABASE_CATALOGUE_BACKUP(2, "数据目录备份"),
    CDC_BACKUP(3, "CDC备份"),
    ;


    private Integer code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (BackupWayEnums c : BackupWayEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.value;
            }
        }
        return null;
    }

    /**
     * 通过值获取代码
     *
     * @param value : 值
     * @return Integer : 代码
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getCode(String value) {
        for (BackupWayEnums c : BackupWayEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return null;
    }


    public static List<Map<String, String>> toList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (BackupWayEnums backupWayEnums: BackupWayEnums.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("code", backupWayEnums.getCode().toString());
            map.put("value", backupWayEnums.getValue());
            list.add(map);
        }
        return list;
    }


    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private BackupWayEnums(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
