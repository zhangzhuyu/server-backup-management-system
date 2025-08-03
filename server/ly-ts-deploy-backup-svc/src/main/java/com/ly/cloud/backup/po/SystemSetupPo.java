package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 系统设置信息表：ly_sm_system_setup
 *
 * @author chenguoqing
 */
@Data
@TableName("ly_sm_system_setup")
public class SystemSetupPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835997L;

    /**
     * 主键
     */

    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 是否文本/1.是，2.否
     */
    @TableField(value = "whether_text")
    private String whetherText;

    /**
     * 键值（kibana ip）
     */
    @TableField(value = "systray")
    private String systray;

    /**
     * 设置键
     */
    @TableField(value = "set_key")
    private String setKey;

    /**
     * 操作人ID
     */
    @TableField(value = "operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 键值2（kibana 端口）
     */
    @TableField(value = "systray2")
    private String systray2;

    /**
     * 键值3（kibana 用户）
     */
    @TableField(value = "systray3")
    private String systray3;

    /**
     * 键值4（kibana 密码）
     */
    @TableField(value = "systray4")
    private String systray4;

    /**
     * 键值5（kibana 版本号）
     */
    @TableField(value = "systray5")
    private String systray5;

}

