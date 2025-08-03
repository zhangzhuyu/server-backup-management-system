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
public enum BackupMethodEnums {

    /**
     * 备份类型
     */
    HTTP_BACKUP(1, "http"),
    SSH_BACKUP(2, "ssh"),
    FTP_BACKUP(3, "ftp"),
    CIFS_BACKUP(4, "cifs")
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
        for (BackupMethodEnums c : BackupMethodEnums.values()) {
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
        for (BackupMethodEnums c : BackupMethodEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return null;
    }


    public static List<Map<String, String>> toList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (BackupMethodEnums backupWayEnums: BackupMethodEnums.values()) {
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
    private BackupMethodEnums(Integer code, String value) {
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
