package com.ly.cloud.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.dto.SystemDeptDto;
import com.ly.cloud.auth.service.SystemDeptService;
import com.ly.cloud.auth.mapper.SystemDeptMapper;
import com.ly.cloud.auth.po.SystemDeptPo;
import com.ly.cloud.auth.vo.SystemDeptVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemDeptServiceImpl extends ServiceImpl<SystemDeptMapper, SystemDeptPo> implements SystemDeptService {
    private static final Logger logger = LoggerFactory.getLogger(SystemDeptServiceImpl.class);

    @Autowired
    private SystemDeptMapper systemDeptMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemDeptDto systemDeptDto) {
        try {
            SystemDeptPo systemDeptPo = new SystemDeptPo();
            BeanUtils.copyProperties(systemDeptDto, systemDeptPo);

            int result = systemDeptMapper.insert(systemDeptPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemDeptDto systemDeptDto) {
        try {
            SystemDeptPo systemDeptPo = new SystemDeptPo();
            BeanUtils.copyProperties(systemDeptDto, systemDeptPo);

            int result = systemDeptMapper.updateById(systemDeptPo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public SystemDeptVo find(Long id) {
        return systemDeptMapper.selectById(id);
    }

    @Override
    public IPage<SystemDeptVo> query(Integer page, Integer pageSize, String deptId, String content) {
        Long id = Long.valueOf(deptId);
        Page<SystemDeptVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 15);
        // 查询当前部门下所有子部门（含当前部门）
        List<SystemDeptVo> systemDeptVos = systemDeptMapper.querySubDeptById(id);
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(systemDeptVos)) {
            systemDeptVos.forEach(systemDeptVo -> {
                if (systemDeptVo.getDeptId() != null || systemDeptVo.getDeptId().longValue() !=0 ) {
                    ids.add(systemDeptVo.getDeptId());
                }
            });
        }
        // 根据id集合查询结果并返回
        return systemDeptMapper.queryByIds(sPage, ids, content);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long[] ids) {
        try {
            for (Long id : ids) {
                int result = systemDeptMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<SystemDeptVo> queryAll() {
        return systemDeptMapper.queryAll();
    }
}
