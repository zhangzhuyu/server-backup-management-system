package com.ly.cloud.quartz.task;

import com.ly.cloud.backup.service.BackupManagementService;
import com.ly.cloud.backup.service.impl.BackupManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
public class BackupManagementScheduled {

    @Autowired
    private BackupManagementService backupManagementService;

    //TODO 待测试完成，再修改调度时间
    //定时清理备份文件和记录，每天0点执行一次
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearBackupFile () {
        if (!BackupManagementServiceImpl.backupClearFlag) {
            backupManagementService.clearBackupFile();
        }
    }

    //定时将备份历史表的状态改为停止备份，每隔一小时执行一次
    @Scheduled(cron = "0 0 * * * ?")
    public void reSetBackupStatus () {
        backupManagementService.reSetBackupStatus();
    }
}
