package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.dto.MiddlewareDto;
import com.ly.cloud.backup.po.MiddlewarePo;
import com.ly.cloud.backup.vo.MiddlewareVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 中间件信息表 Mapper 接口
 * </p>
 *
 * @author chenguoqing
 * @since 2022-08-24
 */
@Mapper
public interface MiddlewareMapper extends BaseMapper<MiddlewarePo> {
    
    /**
     * 按主键查询
     * @param id 主键ID
     * @return 中间件信息表
     */
    MiddlewareVo selectById(Long id);

    /**
     * 查询
     * @param page 分页参数
     * @param middlewareDto 中间件信息表
     * @return 中间件信息表集合
     */
    IPage<MiddlewareVo> select(@Param("page") Page<MiddlewareVo> page, @Param("dto") MiddlewareDto middlewareDto);
    
}
