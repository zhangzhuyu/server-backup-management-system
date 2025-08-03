package com.ly.cloud.backup.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 系统设置信息表
 * @author chenguoqing
 *
 */
@Data
public class SystemSetupDto implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;
    /**
     * 主键
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否文本/1.是，2.否
     */
    private String whetherText;

    /**
     * 键值
     */
    private String systray;

    /**
     * 设置键
     */
    private String setKey;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private java.util.Date operationTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 搜索框输入的内容
     */
    private  String searchParam;
}
