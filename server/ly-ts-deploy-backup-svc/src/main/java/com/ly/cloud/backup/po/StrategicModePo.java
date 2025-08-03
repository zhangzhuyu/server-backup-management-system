package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ly_xn_strategic_mode")
public class StrategicModePo implements Serializable {
    private static final long serialVersionUID = -3043084955318974693L;

    @TableId(value = "id")
    private Long id;

    @TableField(value = "name")
    private String name;
}
