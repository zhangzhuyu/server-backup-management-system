package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.AdapterDto;
import com.ly.cloud.backup.po.AdapterPo;
import com.ly.cloud.backup.vo.AdapterVo;

/**
 * 数据库适配器信息
 * @author chenguoqing
 *
 */
@Mapper
public interface AdapterMapper extends BaseMapper<AdapterPo> {

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 数据库适配器信息
     */
    AdapterVo getDataById(Long id);
    
    /**
     * 查询
     * @param page 分页参数
     * @param adapterDto 数据库适配器信息
     * @return 数据库适配器信息集合
     */
    IPage<AdapterVo> select(@Param("page") Page<AdapterVo> page, @Param("dto")AdapterDto adapterDto);

}