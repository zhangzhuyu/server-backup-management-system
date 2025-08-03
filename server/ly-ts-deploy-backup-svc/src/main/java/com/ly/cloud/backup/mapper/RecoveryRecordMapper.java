package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.RecoveryRecordDto;
import com.ly.cloud.backup.po.RecoveryRecordPo;
import com.ly.cloud.backup.vo.RecoveryRecordVo;

/**
 * 自愈记录信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface RecoveryRecordMapper extends BaseMapper<RecoveryRecordPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 自愈记录信息表
     */
    RecoveryRecordVo selectById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param recoveryRecordDto 自愈记录信息表
     * @return 自愈记录信息表集合
     */
    IPage<RecoveryRecordVo> select(@Param("page") Page<RecoveryRecordVo> page, @Param("dto")RecoveryRecordDto recoveryRecordDto);
}