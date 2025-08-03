package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警字段信息表
 *
 * @author ljb
 */
@Data
public class WarningFieldPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -3190609292510835903L;

    /**
     * 主键
     */
    @TableId(value = "id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 字段中文名
     */
    @TableField(value = "chinese_name")
    private String chineseName;

    /**
     *  字段英文列名
     */
    @TableField(value = "english_name")
    private String englishName;

    /**
     * 所属es表名
     */
    @TableField(value = "es_table")
    private String esTable;



    /**
     * 操作人ID
     */
    @TableField(value = "target_name")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time", fill = FieldFill.INSERT_UPDATE, update = "sysdate()")
    private java.util.Date operationTime;

    /**
     * 备注
     */
    @TableField(value = "target_name")
    private String remark;


}
