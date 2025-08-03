package com.ly.cloud.backup.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ly_xn_test_progress")
public class TestProgressPo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 4472959156322999199L;

    /**
     * 批次
     */
    @TableId(value="pc")
    private String batch;
    /**
     * 检测进度
     */
    @TableField("jcjd")
    private String testSchedule;


}
