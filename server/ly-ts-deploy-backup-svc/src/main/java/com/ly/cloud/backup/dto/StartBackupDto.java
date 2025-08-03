package com.ly.cloud.backup.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StartBackupDto implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 数据库名称
     */
    //private String dataBase;
    /**
     * 表格集合
     */
    private List<String> tablesList;
}
