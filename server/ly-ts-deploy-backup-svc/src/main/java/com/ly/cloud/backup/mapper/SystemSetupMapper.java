package com.ly.cloud.backup.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.SystemSetupDto;
import com.ly.cloud.backup.po.SystemSetupPo;
import com.ly.cloud.backup.vo.SystemSetupVo;

/**
 * 系统设置信息表
 *
 * @author chenguoqing
 */
@Mapper
public interface SystemSetupMapper extends BaseMapper<SystemSetupPo> {

    /**
     * 按主键查询
     *
     * @param id 主键ID
     * @return 系统设置信息表
     */
    public SystemSetupVo getDataById(String id);

    /**
     * 查询
     *
     * @param page           mybatis-plus所提供的分页对象，必须位于第一个参数的位置
     * @param systemSetupDto 系统设置信息表
     * @return 系统设置信息表集合
     */
    public IPage<SystemSetupVo> select(@Param("page") Page<SystemSetupVo> page, @Param("dto")SystemSetupDto systemSetupDto);


    //更新systray的值

    int updateSystray(@Param("systemSetupPo")SystemSetupPo systemSetupPo);
}