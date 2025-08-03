package com.ly.cloud.backup.common.enums;

public enum MiddlewareEnums {

    /**
     * 数据库类型
     */
    REDIS(1, "redis"),
    MONGODB(2, "mongodb"),
    RABBIT(3, "rabbit"),
    ELASTICSEARCH(4, "elasticsearch"),
    DOCKER(5, "docker"),
    ;


    private Integer code;
    private String value;

    MiddlewareEnums(Integer code, String value) {
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

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (MiddlewareEnums c : MiddlewareEnums.values()) {
            if (c.getCode().equals(code)) {
                return c.getValue();
            }
        }
        return null;
    }
}
