package com.ly.cloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.auth.dto.SystemUserDto;
import com.ly.cloud.auth.po.SystemUserPo;
import com.ly.cloud.auth.vo.SystemUserVo;
import com.ly.cloud.backup.dto.UsersExportDto;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author chenguoqing
 * @since 2022-07-26
 */
public interface SystemUserService extends IService<SystemUserPo> {
    /**
     * 添加
     * @param systemUserDto 用户信息表
     */
    void create(SystemUserDto systemUserDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     * @param systemUserDto 用户信息表
     */
    void update(SystemUserDto systemUserDto);

    /**
     * 修改密码
     * @param systemUserDto 用户信息表
     */
    void updatePassword(SystemUserDto systemUserDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 用户信息表
     */
    SystemUserVo find(Long id);

    /**
     * 查询(含子部门用户)
     * @param page
     * @param pageSize
     * @param deptId 部门Id
     * @param content 查询搜索参数
     * @param status 状态
     * @return 用户信息表分页数据
     */
    IPage<SystemUserVo> query(Integer page, Integer pageSize, String deptParentId, String content, String status);

    /**
     * 密码初始加密
     * @return
     */
    void passwordEncryption();

    /**
     * 导入用户数据
     *
     * @param file 文件
     * @return 结果
     */
    String importExcel(MultipartFile file);

    /**
     * 下载模板
     *
     * @param request
     * @param response
     */
    void downloadTemplate(HttpServletRequest request, HttpServletResponse response);

    /**
     * 导出用户数据
     *
     * @param usersExportDto 导出用户传参
     * @param response
     */
    void export(UsersExportDto usersExportDto, HttpServletResponse response);
}
