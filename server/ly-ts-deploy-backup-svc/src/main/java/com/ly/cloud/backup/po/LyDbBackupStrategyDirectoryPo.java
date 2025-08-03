package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "ly_db_backup_strategy_directory")
public class LyDbBackupStrategyDirectoryPo {
    private static final long serialVersionUID = -8719456224941321952L;

    @TableId(value = "id")
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 1 - 类型
     * 2 - 数据源
     * 3 - 方式
     */
    @TableField(value = "type")
    private String type;

    /**
     * 父目录Id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 目录id
     */
    @TableField(value = "directory_id")
    private String directoryId;
}
