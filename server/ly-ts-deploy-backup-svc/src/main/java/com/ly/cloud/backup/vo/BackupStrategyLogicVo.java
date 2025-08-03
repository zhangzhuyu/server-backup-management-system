package com.ly.cloud.backup.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ly.cloud.backup.po.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库适配器信息
 *
 * @author zhangzhuyu
 */
@Data
public class BackupStrategyLogicVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -9003609292510835968L;

    private DatabasePo databasePo;

    private MiddlewarePo middlewarePo;

    private ServerPo serverPo;

    private LyDbBackupStrategyRecordPo lyDbBackupStrategyRecordPo;

    private LyDbBackupHistoryRecordPo historyRecordPo;

    private List<String> tablesList;

    private List<String> backupTargetList;
}
