package com.ly.cloud.backup.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author ljb
 * @Date 2022/7/14
 */

@Data
public class ColumnsVo implements Serializable {
    /**
     * 序列号
     */
    private static final long serialVersionUID = -8003609292510835997L;

    /**
     *  表单标题
     */
    private String title;

    /**
     * 表单数据源
     */
    private String dataIndex;

    /**
     * 宽度
     */
    private String width;

    private boolean ellipsis = true;


    public ColumnsVo(String s, String s1, String s2) {
        this.title = s;
        this.dataIndex = s1;
        this.width = s2;
    }
}
