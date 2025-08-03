package com.ly.cloud.auth.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.auth.constant.UserConstants;
import com.ly.cloud.auth.po.SysRole;
import com.ly.cloud.auth.po.SysRoleMenu;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.auth.po.SysUserRole;
import com.ly.cloud.auth.mapper.SysRoleMapper;
import com.ly.cloud.auth.mapper.SysRoleMenuMapper;
import com.ly.cloud.auth.mapper.SysUserRoleMapper;
import com.ly.cloud.auth.service.ISysRoleService;
import com.ly.cloud.auth.util.SecurityUtils;
import com.ly.cloud.auth.util.SpringUtils;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色 业务层处理
 *
 *
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;


    /**
     * 根据条件分页查询角色数据
     *
     * @param page
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public IPage<SysRole> selectRolePage(Page page, SysRole role) {
        return roleMapper.selectRolePage(page, role);
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询角色
     *
     * @param user_id 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByuser_id(Long user_id) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByuser_id(user_id);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRole_id().longValue() == userRole.getRole_id().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param user_id 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByuser_id(Long user_id) {
        List<SysRole> perms = roleMapper.selectRolePermissionByuser_id(user_id);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRole_key().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param user_id 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByuser_id(Long user_id) {
        return roleMapper.selectRoleListByuser_id(user_id);
    }

    /**
     * 通过角色ID查询角色
     *
     * @param role_id 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long role_id) {
        return roleMapper.selectRoleById(role_id);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role) {
        Long role_id = StringUtils.isNull(role.getRole_id()) ? -1L : role.getRole_id();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRole_name());
        if (StringUtils.isNotNull(info) && info.getRole_id().longValue() != role_id.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role) {
        Long role_id = StringUtils.isNull(role.getRole_id()) ? -1L : role.getRole_id();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRole_key());
        if (StringUtils.isNotNull(info) && info.getRole_id().longValue() != role_id.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (StringUtils.isNotNull(role.getRole_id()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限，暂且定位只有admin账户有权限访问别的角色的权限
     * 其它账户都没有权限，如果有额外逻辑需要添加可以参考ruoyi开源项目的这个切面类
     * com.ruoyi.framework.aspectj.DataScopeAspect，此类对于sql进行了增强。
     *
     * @param role_id 角色id
     */
    @Override
    public void checkRoleDataScope(Long role_id) {
        if (!SysUser.isAdmin(SecurityUtils.getuser_id())) {
            throw new ServiceException("没有权限访问角色数据！");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param role_id 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByrole_id(Long role_id) {
        return userRoleMapper.countUserRoleByrole_id(role_id);
    }

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleMapper.insert(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByrole_id(role.getRole_id());
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }


    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     */
    @Transactional
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        if (!Objects.isNull(role.getmenu_ids())) {
            for (Long menu_id : role.getmenu_ids()) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRole_id(role.getRole_id());
                rm.setMenu_id(menu_id);
                list.add(rm);
            }
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }


    /**
     * 通过角色ID删除角色
     *
     * @param role_id 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(Long role_id) {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByrole_id(role_id);
        return roleMapper.deleteRoleById(role_id);
    }

    /**
     * 批量删除角色信息
     *
     * @param role_ids 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] role_ids) {
        for (Long role_id : role_ids) {
            checkRoleAllowed(new SysRole(role_id));
            checkRoleDataScope(role_id);
            SysRole role = selectRoleById(role_id);
            if (countUserRoleByrole_id(role_id) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRole_name()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(role_ids);
        return roleMapper.deleteRoleByIds(role_ids);
    }

    /**
     * 取消授权用户角色
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    /**
     * 批量取消授权用户角色
     *
     * @param role_id  角色ID
     * @param user_ids 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long role_id, Long[] user_ids) {
        return userRoleMapper.deleteUserRoleInfos(role_id, user_ids);
    }

    /**
     * 批量选择授权用户角色
     *
     * @param role_id  角色ID
     * @param user_ids 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long role_id, Long[] user_ids) {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Long user_id : user_ids) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(user_id);
            ur.setRoleId(role_id);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}
