package com.ly.cloud.dingtalk.component;

/**
 * Class Name: DingCommonEnums Description: 钉钉公共枚举类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月06日 16:22
 * @Copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */

public enum DingCommonEnums {

    /**
     * 按钮排列方向："0"代表竖直
     */
    BUTTON_ORIENTATION_VERTICAL(0,"竖直"),

    /**
     * 按钮排列方向："1"代表水平
     */
    BUTTON_ORIENTATION_HORIZONAL(1,"水平");

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
        for (DingCommonEnums c : DingCommonEnums.values()) {
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
        for (DingCommonEnums c : DingCommonEnums.values()) {
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
     * @author jiangzhongxin
     * @date 2022/3/23 13:42
     */
    DingCommonEnums(Integer code, String value) {
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

