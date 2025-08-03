package com.ly.cloud.backup.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库公共枚举类
 *
 * @author: SYC
 */
public enum BackupStatusEnums {

    /**
     * 数据库类型
     */
    BACKING(0, "正在备份"),
    SUCCESS(1, "备份成功"),
    FAILURE(2, "备份失败"),
    STOP(3, "已停止备份"),
    WAIT(4, "等待定时备份"),
    CLOSING(5, "正在停止备份"),
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
        for (BackupStatusEnums c : BackupStatusEnums.values()) {
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
        for (BackupStatusEnums c : BackupStatusEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return null;
    }


    public static List<Map<String, String>> toList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (BackupStatusEnums dataBaseEnums: BackupStatusEnums.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("code", dataBaseEnums.getCode().toString());
            map.put("value", dataBaseEnums.getValue());
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
    private BackupStatusEnums(Integer code, String value) {
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
