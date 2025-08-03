package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.LyDbBackupTimestampPo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LyDbBackupTimestampMapper extends BaseMapper<LyDbBackupTimestampPo> {
}
