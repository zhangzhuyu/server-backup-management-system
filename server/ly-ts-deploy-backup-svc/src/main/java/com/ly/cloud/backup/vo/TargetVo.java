package com.ly.cloud.backup.vo;

import lombok.Data;

/**
 * @Author ljb
 * @Date 2022/5/30
 */

@Data
public class TargetVo {

    private static final long serialVersionUID = -2336639000426910937L;

    /**
     * ID
     */
    private String Id;

    /**
     * 指标类型
     */
    private String targetType;

    /**
     * 操作符类型
     */
    private String operatorType;

    /**
     * 指标中文名
     */
    private String targetName;

    /**
     * SQL
     */
    private String sqlLine;


}
