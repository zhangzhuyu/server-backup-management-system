package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.dto.SystemInfoDto;
import com.ly.cloud.backup.po.SystemSetupPo;
import com.ly.cloud.backup.dto.SystemSetupDto;
import com.ly.cloud.backup.vo.SystemInfoVo;
import com.ly.cloud.backup.vo.SystemSetupVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 系统设置信息表
 *
 * @author chenguoqing
 */
public interface SystemSetupService extends IService<SystemSetupPo> {

    //获取系统页脚、平台名称等信息
     SystemInfoVo getSystemInfo();

    /**
     * 添加
     *
     * @param systemSetupDto 系统设置信息表
     */
    void create(SystemSetupDto systemSetupDto);

    /**
     * 删除
     *
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);

    /**
     * 更新
     *
     * @param systemSetupDto 系统设置信息表
     */
    boolean updateSystray(SystemSetupDto systemSetupDto);

    /**
     * 按主键查询
     *
     * @param id 主键ID
     * @return 系统设置信息表
     */
    SystemSetupVo find(String id);

    /**
     * 查询
     *
     * @param page
     * @param pageSize
     * @param searchParam 搜索框内容
     * @return 系统设置信息表分页数据
     */
    IPage<SystemSetupVo> query(Integer page,Integer pageSize, String searchParam);

    //上传logo文件
    String uploadLogoFile(MultipartFile uploadlogofile) throws IOException;

    //回显logo图标
    public boolean viewImg(HttpServletResponse response) throws IOException;

    //保存系统设置
    void savaSystemInfo(SystemInfoDto systemInfoDto) throws IllegalAccessException;
}
