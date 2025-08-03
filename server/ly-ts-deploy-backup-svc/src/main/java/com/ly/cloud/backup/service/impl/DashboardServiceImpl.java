package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ly.cloud.backup.dto.DashboardStatsDto;
import com.ly.cloud.backup.mapper.DatabaseMapper;
import com.ly.cloud.backup.mapper.LyDbBackupHistoryRecordMapper;
import com.ly.cloud.backup.mapper.ServerMapper;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.ly.cloud.backup.service.DashboardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private DatabaseMapper lyRmDatabaseMapper;
    @Resource
    private ServerMapper lyRmServerMapper;
    @Resource
    private LyDbBackupHistoryRecordMapper lyDbBackupHistoryRecordMapper;

    @Override
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto statsDto = new DashboardStatsDto();

        // 1. 统计数据库总数 (从 ly_rm_database 表)
        Long dbCount = Long.valueOf(lyRmDatabaseMapper.selectCount(null));
        statsDto.setDatabasesMonitored(dbCount);

        // 2. 统计服务器总数 (从 ly_rm_server 表)
        Long serverCount = Long.valueOf(lyRmServerMapper.selectCount(null));
        statsDto.setServersMonitored(serverCount);

        // 3. 统计今日备份成功和失败数 (从 ly_db_backup_history_record 表)
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // 今天 00:00:00
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);   // 今天 23:59:59

        // 今日成功数 (backup_status = 1)
        Long successToday = Long.valueOf(lyDbBackupHistoryRecordMapper.selectCount(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>()
                .eq(LyDbBackupHistoryRecordPo::getBackupStatus, "1")
                .between(LyDbBackupHistoryRecordPo::getBackupTime, todayStart, todayEnd)
        ));
        statsDto.setBackupsToday(successToday);

        // 今日失败数 (backup_status = 2)
        Long failuresToday = Long.valueOf(lyDbBackupHistoryRecordMapper.selectCount(new LambdaQueryWrapper<LyDbBackupHistoryRecordPo>()
                .eq(LyDbBackupHistoryRecordPo::getBackupStatus, "2")
                .between(LyDbBackupHistoryRecordPo::getBackupTime, todayStart, todayEnd)
        ));
        statsDto.setFailuresToday(failuresToday);

        return statsDto;
    }
}
