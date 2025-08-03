package com.ly.cloud.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.dto.SystemRoleDto;
import com.ly.cloud.auth.dto.SystemUserRoleDto;
import com.ly.cloud.auth.service.SystemUserRoleService;
import com.ly.cloud.auth.mapper.SystemUserRoleMapper;
import com.ly.cloud.auth.po.SystemUserRolePo;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.auth.vo.SystemUserRoleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户和角色关联表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemUserRoleServiceImpl extends ServiceImpl<SystemUserRoleMapper, SystemUserRolePo> implements SystemUserRoleService {
    private static final Logger logger = LoggerFactory.getLogger(SystemUserRoleServiceImpl.class);

    @Autowired
    private SystemUserRoleMapper systemUserRoleMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemUserRoleDto systemUserRoleDto) {
        try {
            SystemUserRolePo systemUserRolePo = new SystemUserRolePo();
            BeanUtils.copyProperties(systemUserRoleDto, systemUserRolePo);

            int result = systemUserRoleMapper.insert(systemUserRolePo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemUserRoleDto systemUserRoleDto) {
        try {
            SystemUserRolePo systemUserRolePo = new SystemUserRolePo();
            BeanUtils.copyProperties(systemUserRoleDto, systemUserRolePo);

            int result = systemUserRoleMapper.updateById(systemUserRolePo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public SystemUserRoleVo find(Long id) {
        return systemUserRoleMapper.selectById(id);
    }

    @Override
    public IPage<SystemUserRoleVo> query(int pageNum, int pageSize, SystemUserRoleDto systemUserRoleDto) {
        try {
            Page<SystemUserRoleVo> page = new Page<>(pageNum > 0 ? pageNum : 1, pageSize > 0 ? pageSize : 15);
            return systemUserRoleMapper.select(page, systemUserRoleDto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = systemUserRoleMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
