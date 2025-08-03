package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.po.LyDbBackupStrategyRecordPo;
import com.ly.cloud.backup.vo.LyDbBackupStrategyRecordVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LyDbBackupStrategyRecordMapper extends BaseMapper<LyDbBackupStrategyRecordPo> {
    /**
     * 单个查询告警规则信息
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:12
     * @param id : 规则id
     * @param status : 是否启用 0 或 1
     * @return: Integer ：操作记录数
     */
    int enableStrategyRecordById(@Param("id") String id, @Param("status") String status, @Param("param") String param);

    int updateStrategyRecordById(@Param("id") String id, @Param("param") String param);

    int changeStrategyRecordById(@Param("id") String id, @Param("param") String param);

    int enableStrategyRecordById1(@Param("id") String id, @Param("status") String status);

    int enableStrategyRecordById2(@Param("id") String id, @Param("param") String param);

    String selectStrategyRecordById(@Param("id") String id);
}

































