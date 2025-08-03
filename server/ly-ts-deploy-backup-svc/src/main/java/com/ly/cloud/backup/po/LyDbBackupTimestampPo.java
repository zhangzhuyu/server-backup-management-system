package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 备份名字时间戳表
 * @author zhangzhuyu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ly_db_backup_timestamp")
public class LyDbBackupTimestampPo implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 名字
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 名字
     */
    @TableField(value = "time_stamp")
    private String timeStamp;
}













































