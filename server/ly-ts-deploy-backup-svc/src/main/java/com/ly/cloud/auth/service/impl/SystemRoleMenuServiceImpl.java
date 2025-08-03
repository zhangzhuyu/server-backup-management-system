package com.ly.cloud.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.dto.SystemRoleMenuDto;
import com.ly.cloud.auth.service.SystemRoleMenuService;
import com.ly.cloud.auth.mapper.SystemRoleMenuMapper;
import com.ly.cloud.auth.po.SystemRoleMenuPo;
import com.ly.cloud.auth.vo.SystemRoleMenuVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemRoleMenuServiceImpl extends ServiceImpl<SystemRoleMenuMapper, SystemRoleMenuPo> implements SystemRoleMenuService {
    private static final Logger logger = LoggerFactory.getLogger(SystemRoleMenuServiceImpl.class);

    @Autowired
    private SystemRoleMenuMapper systemRoleMenuMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemRoleMenuDto systemRoleMenuDto) {
        try {
            SystemRoleMenuPo systemRoleMenuPo = new SystemRoleMenuPo();
            BeanUtils.copyProperties(systemRoleMenuDto, systemRoleMenuPo);

            int result = systemRoleMenuMapper.insert(systemRoleMenuPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemRoleMenuDto systemRoleMenuDto) {
        try {
            SystemRoleMenuPo systemRoleMenuPo = new SystemRoleMenuPo();
            BeanUtils.copyProperties(systemRoleMenuDto, systemRoleMenuPo);

            int result = systemRoleMenuMapper.updateById(systemRoleMenuPo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public SystemRoleMenuVo find(Long id) {
        return systemRoleMenuMapper.selectById(id);
    }

    @Override
    public IPage<SystemRoleMenuVo> query(int pageNum, int pageSize, SystemRoleMenuDto systemRoleMenuDto) {
        try {
            Page<SystemRoleMenuVo> page = new Page<>(pageNum > 0 ? pageNum : 1, pageSize > 0 ? pageSize : 15);
            return systemRoleMenuMapper.select(page, systemRoleMenuDto);
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
                int result = systemRoleMenuMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
