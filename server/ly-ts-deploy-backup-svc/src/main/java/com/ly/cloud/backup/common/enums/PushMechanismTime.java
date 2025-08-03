package com.ly.cloud.backup.common.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息推送机制时间枚举类
 */

public enum PushMechanismTime {
    MINUTE(1,"min",60*1000L),
    HOUR(2,"h", 60*60*1000L),
    DAY(3, "day", 24*60*60*1000L);

    private Integer code;
    private String unit;
    private Long value;

    PushMechanismTime(Integer code, String unit, Long value) {
        this.code = code;
        this.unit = unit;
        this.value = value;
    }

    /**
     * 根据code获取Unit
     * @param code
     * @return
     */
    public static String getUnitByCode(Integer code) {
        PushMechanismTime[] values = PushMechanismTime.values();
        for (PushMechanismTime value : values) {
            if (value.code.equals(code)) {
                return value.unit;
            }
        }
        return null;
    }

    /**
     * 根据code获取Value
     * @param code
     * @return
     */
    public static Long getValueByCode(Integer code) {
        PushMechanismTime[] values = PushMechanismTime.values();
        for (PushMechanismTime value : values) {
            if(value.code.equals(code)) {
                return value.value;
            }
        }
        return null;
    }

    /**
     * 获取该枚举类所有Unit
     * @return
     */
    public static List<String> getAllUnit() {
        PushMechanismTime[] values = PushMechanismTime.values();
        List<String> list = new ArrayList<>();
        for (PushMechanismTime value : values) {
            list.add(value.unit);
        }
        return list;
    }

    /**
     * 根据unit获取Value
     * @param unit
     * @return
     */
    public static Long getValueByUnit(String unit) {
        PushMechanismTime[] values = PushMechanismTime.values();
        for (PushMechanismTime value : values) {
            if(value.unit.equals(unit)) {
                return value.value;
            }
        }
        return null;
    }


}
