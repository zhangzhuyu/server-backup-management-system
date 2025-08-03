package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.vo.BackupDirectoryTreeVo;
import com.ly.cloud.backup.vo.LyDbBackupHistoryDetailsVo;
import com.ly.cloud.backup.vo.LyDbBackupLevitatedSphereVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface LyDbBackupHistoryRecordService extends IService<LyDbBackupHistoryRecordPo>{


    /**
     * 下载数据备份文件
     * @param filename
     * @return
     */
    void  downloadBackupFile(String id, HttpServletResponse response);

    List<BackupDirectoryTreeVo> getTypeSourceWayList();

    IPage<LyDbBackupHistoryRecordPo> queryBackupHistoryList(Integer page, Integer pageSize, String title, String totalMethod, String value, String strategyId,String authDeptId);


    IPage<LyDbBackupHistoryRecordPo> get(Integer page, Integer pageSize, String id, String title, String authDeptId);

    LyDbBackupHistoryDetailsVo details(String id);

    LyDbBackupLevitatedSphereVo levitatedSphereDetails();
}
