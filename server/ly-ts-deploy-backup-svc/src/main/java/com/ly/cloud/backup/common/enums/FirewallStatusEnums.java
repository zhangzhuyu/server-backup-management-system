package com.ly.cloud.backup.common.enums;

/**
 * jvm公共枚举类
 *
 * @author: SYC
 */
public enum FirewallStatusEnums {

    /**
     * 垃圾回收类型 PS MarkSweep|PS Scanvenge
     */
    FIREWALL_STATUS_TRUE(true, "开启"),
    FIREWALL_STATUS_FALSE(false, "关闭"),
    ;


    private boolean code;
    private String value;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(boolean code) {
        if (code){
            return FIREWALL_STATUS_TRUE.getValue();
        }else {
            return FIREWALL_STATUS_FALSE.getValue();
        }
    }

    /**
     * 通过值获取代码
     *
     * @param value : 值
     * @return Integer : 代码
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static boolean getCode(String value) {
        for (FirewallStatusEnums c : FirewallStatusEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return false;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private FirewallStatusEnums(boolean code, String value) {
        this.code = code;
        this.value = value;
    }

    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
