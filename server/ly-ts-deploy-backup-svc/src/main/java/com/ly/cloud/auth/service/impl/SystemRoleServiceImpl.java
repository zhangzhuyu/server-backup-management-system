package com.ly.cloud.auth.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.dto.SystemRoleMenuDto;
import com.ly.cloud.auth.dto.SystemUserRoleDto;
import com.ly.cloud.auth.excel.SystemRoleStandardEntity;
import com.ly.cloud.auth.mapper.*;
import com.ly.cloud.auth.po.SystemRoleMenuPo;
import com.ly.cloud.auth.po.SystemUserRolePo;
import com.ly.cloud.auth.service.SystemRoleMenuService;
import com.ly.cloud.auth.service.SystemRoleService;
import com.ly.cloud.auth.util.DateUtils;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.auth.dto.SystemRoleDto;
import com.ly.cloud.auth.po.SystemRolePo;
import com.ly.cloud.auth.vo.SystemMenuVo;
import com.ly.cloud.auth.vo.SystemRoleVo;
import com.ly.cloud.auth.vo.SystemUserVo;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRolePo> implements SystemRoleService {
    private static final Logger logger = LoggerFactory.getLogger(SystemRoleServiceImpl.class);

    @Autowired
    private SystemRoleMapper systemRoleMapper;

    @Autowired
    private SystemRoleMenuMapper systemRoleMenuMapper;

    @Autowired
    private SystemUserRoleMapper systemUserRoleMapper;

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private SystemMenuMapper systemMenuMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemRoleDto systemRoleDto) {
        try {
            SystemRolePo systemRolePo = new SystemRolePo();
            BeanUtils.copyProperties(systemRoleDto, systemRolePo);
            systemRolePo.setCreateBy("admin");
            systemRolePo.setCreateTime(new Date());
            // 判断role_sort是否为空，没有的话默认传1（字段为空时插入会报错）
            if (systemRolePo.getRoleSort() == null) {
                systemRolePo.setRoleSort(1);
            }
            systemRoleMapper.insert(systemRolePo);
            if (StringUtils.isNotEmpty(systemRoleDto.getMenuPermissions())) {
                List<String> menuPermissions = systemRoleDto.getMenuPermissions();
                menuPermissions.forEach(item -> {
                    SystemRoleMenuPo systemRoleMenuPo = new SystemRoleMenuPo();
                    systemRoleMenuPo.setRoleId(systemRolePo.getRoleId());
                    systemRoleMenuPo.setMenuId(Long.valueOf(item));
                    systemRoleMenuMapper.insert(systemRoleMenuPo);
                });
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemRoleDto systemRoleDto) {
        try {
            SystemRolePo systemRolePo = new SystemRolePo();
            BeanUtils.copyProperties(systemRoleDto, systemRolePo);

            systemRolePo.setUpdateBy("admin");
            systemRolePo.setUpdateTime(new Date());
            int result = systemRoleMapper.updateById(systemRolePo);

            // 删除所有跟角色id 关联的权限信息
            systemRoleMenuMapper.delete(new QueryWrapper<SystemRoleMenuPo>().lambda().eq(SystemRoleMenuPo::getRoleId, systemRoleDto.getRoleId()));

            if (StringUtils.isNotEmpty(systemRoleDto.getMenuPermissions())) {
                List<String> menuPermissions = systemRoleDto.getMenuPermissions();
                menuPermissions.forEach(item -> {

                    if(!StringUtils.isNumeric(item)){
                        throw new RuntimeException("角色权限Id错误！");
                    }
                    SystemRoleMenuPo systemRoleMenuPo = new SystemRoleMenuPo();
                    systemRoleMenuPo.setRoleId(systemRolePo.getRoleId());
                    systemRoleMenuPo.setMenuId(Long.valueOf(item));
                    systemRoleMenuMapper.insert(systemRoleMenuPo);
                });
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public SystemRoleVo find(Long id) {
        return systemRoleMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void assigningUsers(SystemUserRoleDto systemUserRoleDto) {
        // 先删除所有
        systemUserRoleMapper.delete(new QueryWrapper<SystemUserRolePo>().lambda().eq(SystemUserRolePo::getRoleId,systemUserRoleDto.getRoleId()));

        List<String> userIds = systemUserRoleDto.getUserIds();
        if(StringUtils.isNotEmpty(userIds)){
            userIds.forEach(item->{
                SystemUserRolePo systemUserRolePo=new SystemUserRolePo();
                systemUserRolePo.setUserId(Long.valueOf(item));
                systemUserRolePo.setRoleId(systemUserRoleDto.getRoleId());
                systemUserRoleMapper.insert(systemUserRolePo);
            });
        }

    }

    @Override
    public IPage<SystemRoleVo> query(Integer page, Integer pageSize, String roleName, String status) {
        try {
            Page<SystemRoleVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 15);
            List<String> statusList = null;
            if (StringUtils.isNotEmpty(status)) {
                String[] arr = status.split(",");
                statusList =Arrays.asList(arr);
            }
            SystemRoleDto systemRoleDto = new SystemRoleDto();
            if (StringUtils.isNotEmpty(roleName)) systemRoleDto.setRoleName(roleName);
            IPage<SystemRoleVo> voIPage = systemRoleMapper.select(sPage, systemRoleDto, statusList);
            List<SystemRoleVo> records = voIPage.getRecords();
            records.forEach(item -> {
                List<String> menuPermissions = systemRoleMenuMapper.selectMenuIdById(item.getRoleId());
                item.setMenuPermissions(menuPermissions);
            });
            return voIPage;
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
                int result = systemRoleMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void export(List<Long> roleIds, HttpServletResponse response) {
        // 查询数据
        QueryWrapper<SystemRolePo> queryWrapper = new QueryWrapper<SystemRolePo>();
        if(roleIds != null && roleIds.size() > 0) {
            queryWrapper.in("role_id", roleIds);
        }
        //排序
        queryWrapper.orderByAsc("role_sort").orderByDesc("create_time").orderByDesc("role_id");
        List<SystemRolePo> list = systemRoleMapper.selectList(queryWrapper);
        List<SystemRoleStandardEntity> listEntity = new ArrayList<SystemRoleStandardEntity>();
        // 将Po数据copy至Entity
        for(SystemRolePo po : list) {
            SystemRoleStandardEntity entity = new SystemRoleStandardEntity();
            BeanUtils.copyProperties(po, entity);
            listEntity.add(entity);
        }
        // 设置格式和标题
        ExportParams params = new ExportParams();
        params.setCreateHeadRows(true);
        params.setTitle("角色信息");
        params.setType(ExcelType.XSSF);
        // 导出
        Workbook workbook = ExcelExportUtil.exportExcel(params, SystemRoleStandardEntity.class, listEntity);
        OutputStream outputStream = null;
        // 文件名
        String fileName = "角色信息"+DateUtils.dateTime()+".xlsx";
        try {
            // 设置文件ContentType类型
            response.setContentType("application/octet-stream;charset=UTF-8");
            // 设置文件头
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("角色信息导出文件错误!");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public IPage<SystemUserVo> queryUsersByRoleId(Integer page, Integer pageSize, String id) {
        Page<SystemUserVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 15);
        return systemUserMapper.selectUsersByRoleId(sPage, id);
    }

    @Override
    public List<SystemMenuVo> queryMenusByRoleId(String id) {
        Long roleId = Long.valueOf(id);
        // 根据角色Id获取权限菜单集
        List<SystemMenuVo> menus = systemMenuMapper.selectByRoleId(roleId);
        // 两层循环实现将菜单集list转换成树图结构
        List<SystemMenuVo> trees = new ArrayList<SystemMenuVo>();
        menus.forEach(item->{
            // 获取集合中的父节点，开发测试时直接写成parentId为"1"即为集合中的根节点，正式环境视数据库设计而定
            if (1L == item.getParentId().longValue()) {
                trees.add(item);
            }
            menus.forEach(systemMenuVo -> {
                if (systemMenuVo.getParentId().equals(item.getMenuId())) {
                    if (item.getChildren() == null) {
                        item.setChildren(new ArrayList<SystemMenuVo>());
                    }
                    item.getChildren().add(systemMenuVo);
                }
            });
        });
        return trees;
    }
}
