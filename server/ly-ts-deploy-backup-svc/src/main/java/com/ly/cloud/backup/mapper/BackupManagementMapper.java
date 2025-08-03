package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.BackupManagementPo;
import com.ly.cloud.backup.vo.BackupManagementVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BackupManagementMapper extends BaseMapper<BackupManagementPo> {

    List<BackupManagementVo> selectBackupManagementByBackupWayList(@Param("backupWay") String backupWay,@Param("authDeptId") String authDeptId);
}
