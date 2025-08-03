package com.ly.cloud.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.cloud.auth.dto.SystemMenuDto;
import com.ly.cloud.auth.service.SystemMenuService;
import com.ly.cloud.auth.mapper.SystemMenuMapper;
import com.ly.cloud.auth.po.SystemMenuPo;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.auth.vo.SystemMenuVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
@Service
public class SystemMenuServiceImpl extends ServiceImpl<SystemMenuMapper, SystemMenuPo> implements SystemMenuService {
    private static final Logger logger = LoggerFactory.getLogger(SystemMenuServiceImpl.class);

    @Autowired
    private SystemMenuMapper systemMenuMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(SystemMenuDto systemMenuDto) {
        try {
            SystemMenuPo systemMenuPo = new SystemMenuPo();
            BeanUtils.copyProperties(systemMenuDto, systemMenuPo);

            int result = systemMenuMapper.insert(systemMenuPo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(SystemMenuDto systemMenuDto) {
        try {
            SystemMenuPo systemMenuPo = new SystemMenuPo();
            BeanUtils.copyProperties(systemMenuDto, systemMenuPo);

            int result = systemMenuMapper.updateById(systemMenuPo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    @Override
    public SystemMenuVo find(Long id) {
        return systemMenuMapper.selectById(id);
    }

    @Override
    public IPage<SystemMenuVo> query(int pageNum, int pageSize, SystemMenuDto systemMenuDto) {
        try {
            Page<SystemMenuVo> page = new Page<>(pageNum > 0 ? pageNum : 1, pageSize > 0 ? pageSize : 15);
//            return systemMenuMapper.select(page, systemMenuDto);
            return systemMenuMapper.queryTreeSystemMenuAll(page, systemMenuDto);
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
                int result = systemMenuMapper.deleteById(id);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public IPage<SystemMenuVo> querySubmenus(Integer page, Integer pageSize, String id, String status, String content) {
        Long menuId = Long.valueOf(id);
        Page<SystemMenuVo> sPage = new Page<>(page > 0 ? page : 1, pageSize > 0 ? pageSize : 15);
        // 查询当前菜单下所有子菜单（含当前菜单）
        List<SystemMenuVo> systemMenuVos = systemMenuMapper.querySubmenusById(menuId);
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(systemMenuVos)) {
            systemMenuVos.forEach(systemMenuVo -> {
                if (systemMenuVo.getMenuId() != null || systemMenuVo.getMenuId().longValue() !=0 ) {
                    ids.add(systemMenuVo.getMenuId());
                }
            });
        }
        // 状态集合
        List<String> statusList = null;
        if (StringUtils.isNotEmpty(status)) {
            String[] arr = status.split(",");
            statusList =Arrays.asList(arr);
        }
        // 根据id集合和状态查询结果并返回
        IPage<SystemMenuVo> systemMenuVoIPage = systemMenuMapper.queryByIds(sPage, ids, statusList, content);
        return systemMenuVoIPage;
    }

    @Override
    public List<SystemMenuVo> queryAll() {
        return systemMenuMapper.queryAll();
    }



    /*private List<SystemMenuVo> treeList = new ArrayList<>();  //全局变量

//    @Override
    public List<SystemMenuVo> getTree() {
        //先获取到所有数据
        QueryWrapper<SystemMenuPo> queryWrapper = new QueryWrapper<SystemMenuPo>();
        List<SystemRolePo> list = systemRoleMapper.selectList(queryWrapper);
        treeList=systemMenuMapper.selectList();
        if(treeList==null) return null;

        //获取到所有一级节点
        List<SystemMenuVo> parentList = this.SystemMenuVoMapper.findParentList();
        List<SystemMenuVo> list = new ArrayList<>();
        if(parentList != null){
            for (int i = 0; i < parentList.size(); i++) {
                list.add(recursiveTree(parentList.get(i).getId()));
            }
        }
        return list;
    }


    *//**
     * 递归算法解析成树形结构
     * @param cid
     *//*
    public SystemMenuVo recursiveTree(Integer cid) {
        SystemMenuVo node = getSystemMenuVoById(cid);
        List<SystemMenuVo> childTreeNodes  = getChildTreeById(cid);
        for(SystemMenuVo child : childTreeNodes){
            SystemMenuVo n = recursiveTree(child.getId());
            node.getChildren().add(n);
        }
        return node;
    }

    *//**
     * 根据CID查询节点对象
     *//*
    public SystemMenuVo getSystemMenuVoById(Integer cid){
        Map map =  getTreeMap();
        return (SystemMenuVo) map.get(cid);
    }

    *//**
     * 一次性取所有数据，为了减少对数据库查询操作
     * @return
     *//*
    public Map getTreeMap(){
        Map map =  new HashMap<Integer, SystemMenuVo>();
        if(null != treeList){
            for(SystemMenuVo d : treeList){
                map.put(d.getId(), d);
            }
        }
        return map;
    }

    *//**
     * 根据父节点CID获取所有了节点
     *//*
    public List<SystemMenuVo> getChildTreeById(Integer cid){
        List<SystemMenuVo> list = new ArrayList<>();
        if(null != treeList){
            for (SystemMenuVo d : treeList) {
                if(null != cid){
                    if (cid.equals(d.getParentId())) {
                        list.add(d);
                    }
                }
            }
        }
        return list;
    }*/
}
