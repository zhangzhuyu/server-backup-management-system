package com.ly.cloud.backup.common.handler.batchandle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 根Mapper，给表Mapper继承用的，可以自定义通用方法
 * {@link com.baomidou.mybatisplus.core.mapper.BaseMapper}
 * {@link com.baomidou.mybatisplus.extension.service.IService}
 * {@link com.baomidou.mybatisplus.extension.service.impl.ServiceImpl}
 *
 * @author admin
 */
public interface CustomMapper<T> extends BaseMapper<T> {

    /**
     * 自定义批量插入
     */
    Integer insertBatch(@Param("list") List<T> list);

    /**
     * 自定义批量更新，条件为主键
     */
    Integer updateBatch(@Param("list") List<T> list);

}
 