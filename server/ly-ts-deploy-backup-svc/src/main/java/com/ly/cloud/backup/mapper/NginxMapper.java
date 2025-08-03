package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.dto.NginxDto;
import com.ly.cloud.backup.po.NginxPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.cloud.backup.vo.NginxVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * nginx信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface NginxMapper extends BaseMapper<NginxPo> {
    /**
     * 查询
     * @param page 分页参数
     * @param nginxDto nginx信息表
     * @return nginx信息表集合
     */
    IPage<NginxVo> select(@Param("page") Page<NginxVo> page, @Param("dto")NginxDto nginxDto);

    /**
     * 查询
     * @param nginxDto nginx信息表
     * @return nginx信息表集合
     */
    List<NginxVo> selecAllById(@Param("dto")NginxDto nginxDto);

    /**
     * @title: getAllData
     * @description: 查询所有数据
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public List<NginxVo> getAllData();
}