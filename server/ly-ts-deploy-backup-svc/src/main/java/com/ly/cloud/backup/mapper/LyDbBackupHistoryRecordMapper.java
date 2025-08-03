package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.LyDbBackupStrategyRecordDto;
import com.ly.cloud.backup.po.LyDbBackupHistoryRecordPo;
import com.ly.cloud.backup.po.LyDbBackupStrategyDirectoryPo;
import com.ly.cloud.backup.vo.BackupDirectoryTreeVo;
import com.ly.cloud.backup.vo.WarningFieldVo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface LyDbBackupHistoryRecordMapper extends BaseMapper<LyDbBackupHistoryRecordPo> {

    List<BackupDirectoryTreeVo> getTypeSourceWayList();

    List<LyDbBackupHistoryRecordPo> queryBackupHistoryList(@Param("content") String title, @Param("totalMethodList") List<String> totalMethodList, @Param("list") List<String> ids,@Param("authDeptId") String authDeptIdString ,@Param("strategyId")String strategyId);

    List<LyDbBackupStrategyDirectoryPo> selectBackupHistoryList();

    List<String> selectBackupStrategyRecordListByCondition (@Param("dto") LyDbBackupStrategyRecordDto lyDbBackupStrategyRecordDto);

    IPage<LyDbBackupHistoryRecordPo> queryBackupHistoryListPage(@Param("page") Page<LyDbBackupHistoryRecordPo> page1, @Param("content") String title, @Param("list") List<String> list,@Param("authDeptId") String authDeptId);

    @Flush
    @Insert(" insert into ly_db_backup_strategy_record (id) values (${id}) ")
    void insertValue(@Param("id") String id,@Param("title") String title);

    @Select("select chinese_name chineseName , english_name englishName from ly_mw_warning_field ${ew.customSqlSegment}")
    List<WarningFieldVo> selectEnZhList(@Param(Constants.WRAPPER) QueryWrapper<WarningFieldVo> wrapper);

    LyDbBackupHistoryRecordPo selectHistoryRecordById(@Param("id") String id);

    //根据策略id和备份状态 更改备份时间
    int updateBackupTime(@Param("nextTime") Date nextTime, @Param("id") String id);

    int deleteByStrategyIdAndStatus(@Param("id") String id);
}