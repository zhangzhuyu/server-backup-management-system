package com.ly.cloud.backup.common.enums;

/**
 * 资源管理枚举类
 *
 * @author: SYC
 */
public enum ResourceManagementEnums {

    /**
     * （服务器:1、数据库:2、服务:3、nginx:4、防火墙:5、应用：6）
     */
    SERVER(1, "server","服务器"),
    DATABASE(2, "database","数据库"),
    SERVICE(3, "service","服务"),
    NGINX(5, "nginx","nginx"),
    FIREWALLD(6, "firewalld","防火墙"),
    APPLICATION(4, "application","应用")

    ;

    private Integer code;
    private String value;
    private String name;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getName(Integer code) {
        for (ResourceManagementEnums c : ResourceManagementEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.name;
            }
        }
        return null;
    }

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     */
    public static String getValue(Integer code) {
        for (ResourceManagementEnums c : ResourceManagementEnums.values()) {
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
     */
    public static Integer getCode(String value) {
        for (ResourceManagementEnums c : ResourceManagementEnums.values()) {
            if (value.equals(c.getValue())) {
                return c.getCode();
            }
        }
        return null;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param value : 值
     */
    private ResourceManagementEnums(Integer code, String value,String name) {
        this.code = code;
        this.value = value;
        this.name = name;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
