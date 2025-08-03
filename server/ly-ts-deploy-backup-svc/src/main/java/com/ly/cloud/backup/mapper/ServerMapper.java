package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.ServerDto;
import com.ly.cloud.backup.po.ServerPo;
import com.ly.cloud.backup.vo.ServerVo;

/**
 * 服务器信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface ServerMapper extends BaseMapper<ServerPo> {



    /**
     * 按主键查询
     * @param id 主键ID
     * @return 服务器信息表
     */
    ServerVo selectById(Long id);

    /**
     * 查询
     * @param page 分页参数
     * @param serverDto 服务器信息表
     * @return 服务器信息表集合
     */
    IPage<ServerVo> select(@Param("page") Page<ServerVo> page, @Param("dto")ServerDto serverDto);

    /**
     * 查询
     * @param serverDto 服务器信息表
     * @return 服务器信息表集合
     */
    List<ServerVo> selectByAll(@Param("dto")ServerDto serverDto);

    /**
     * @title: getAllData
     * @description: 查询所有数据
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    List<ServerVo> getAllData();

    /**
     *  更新状态 异常
     * @param list  ids
     * @return
     */
    Integer updateStatusErr(@Param("list") List<String> list);
    Integer updateStatusSuccess(@Param("list") List<String> list);

    /**
     *  更新状态 健康
     * @param list
     * @return
     */
    Integer updateStatusSucc(@Param("list") List<String> list);

    /**
     * 获取nginx的IP地址
     */
    String getNginxIpByAppUrl(@Param("appDomain") String appDomain);

}