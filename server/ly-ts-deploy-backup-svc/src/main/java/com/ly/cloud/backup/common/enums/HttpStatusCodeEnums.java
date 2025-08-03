package com.ly.cloud.backup.common.enums;

import java.util.Objects;

/**
 * 请求状态枚举类
 *
 * @author: jiangzhongxin
 */
public enum HttpStatusCodeEnums {

    /**
     * 200 请求成功
     */
    REQUEST_SUCCEEDED(200, "请求成功"),
    /**
     * 301 永久重定向
     */
    PERMANENT_REDIRECT(301, "永久重定向"),
    /**
     * 302 临时重定向
     */
    TEMPORARY_REDIRECT(302, "临时重定向"),
    /**
     * 304 未修改重定向
     */
    UNMODIFIED_REDIRECT(304, "未修改重定向"),
    /**
     * 400 错误请求
     */
    WRONG_REQUEST(400, "错误请求"),
    /**
     * 401 未经授权
     */
    UNACCREDITED(401, "未经授权"),
    /**
     * 403 禁止访问
     */
    ACCESS_DENIED(403, "禁止访问"),
    /**
     * 404 文件未找到
     */
    FILE_NOT_FOUND(404, "文件未找到"),
    /**
     * 500 内部服务器错误
     */
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    /**
     * 502 无效网关
     */
    INVALID_GATEWAY(502, "无效网关"),
    /**
     * null 其他
     */
    OTHER(null, "其他");

    private Integer code;
    private String description;

    /**
     * 通过代码获取值
     *
     * @param code : 代码
     * @return String : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static String getValue(Integer code) {
        for (HttpStatusCodeEnums c : HttpStatusCodeEnums.values()) {
            if (Objects.equals(c.getCode(), code)) {
                return c.description;
            }
        }
        return null;
    }

    /**
     * 通过值获取代码
     *
     * @param description : 值
     * @return Integer : 代码
     * @author jiangzhongxin
     * @date 2022/3/23 13:41
     */
    public static Integer getCode(String description) {
        for (HttpStatusCodeEnums c : HttpStatusCodeEnums.values()) {
            if (description.equals(c.getDescription())) {
                return c.getCode();
            }
        }
        return null;
    }

    /**
     * 构造方法
     *
     * @param code  : 代码
     * @param description : 值
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    private HttpStatusCodeEnums(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
