package com.ly.cloud.backup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.backup.mapper.LyDbBackupTimestampMapper;
import com.ly.cloud.backup.po.LyDbBackupTimestampPo;
import com.ly.cloud.backup.service.LyDbBackupTimestampService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 数据库备份时间戳信息表
 *
 * @author zhangzhuyu
 */
@Service
public class LyDbBackupTimestampServiceImpl extends ServiceImpl<LyDbBackupTimestampMapper, LyDbBackupTimestampPo> implements LyDbBackupTimestampService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseServiceImpl.class);
}
