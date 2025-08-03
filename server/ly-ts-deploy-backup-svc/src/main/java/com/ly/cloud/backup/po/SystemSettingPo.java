package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("system_setting")
public class SystemSettingPo {

    @TableId(value = "id")
    private Long id;

    @TableField(value = "setting_key")
    private String settingKey;

    @TableField(value = "setting_value")
    private String settingValue;

    @TableField(value = "description")
    private String description;
}
