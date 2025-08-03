package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.dto.BackupManagementDto;
import com.ly.cloud.backup.dto.ServerInfoDto;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.vo.BackupManagementVo;
import com.ly.cloud.backup.vo.StrategicModeVo;

import java.util.List;

public interface BackupManagementService extends IService<BackupManagementPo> {
    List<StrategicModeVo> findClientList(String totalMethod);

    List<BackupManagementPo> getBackupManagementList(String backupWay, String authDeptId);

    void updateBackupManagement(BackupManagementDto backupManagementDto);

   void clearBackupFile();

   void reSetBackupStatus();

    String Checkcorrect(Long serverId, String dataSourceType);

    void insertBackupManagement(BackupManagementDto backupManagementDto);

    void testConnection(BackupManagementDto backupManagementDto);

    void delete(Long[] ids);

    List<ServerInfoDto> getServerInfoList();

}
