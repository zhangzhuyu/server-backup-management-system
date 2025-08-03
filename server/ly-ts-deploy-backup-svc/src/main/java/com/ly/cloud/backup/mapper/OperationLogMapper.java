package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ly.cloud.backup.po.OperationLogPo;
import com.ly.cloud.backup.vo.OperationLogVo;

/**
 * 操作日志信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogPo> {


    /**
     * 按主键查询
     * @param id 主键ID
     * @return 操作日志信息表
     */
    OperationLogVo selectById(String id);
    
//    /**
//     * 查询
//     * @param page 分页参数
//     * @param operationLogDto 操作日志信息表
//     * @return 操作日志信息表集合
//     */
//    IPage<OperationLogVo> select(@Param("page") Page<OperationLogVo> page, @Param("dto")OperationLogDto operationLogDto);
}