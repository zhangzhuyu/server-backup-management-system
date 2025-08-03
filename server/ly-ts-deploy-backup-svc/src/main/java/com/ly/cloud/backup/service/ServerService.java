package com.ly.cloud.backup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.cloud.backup.dto.ServerInfoDto;
import com.ly.cloud.backup.po.ServerPo;
import com.ly.cloud.backup.dto.ServerDto;
import com.ly.cloud.backup.vo.ServerVo;

import java.util.List;

/**
 * 服务器信息表
 * @author chenguoqing
 *
 */
public interface ServerService extends IService<ServerPo> {

    /**
     * 添加
     * @param serverDto 服务器信息表
     */
    void create(ServerDto serverDto);

    /**
     * 删除
     * @param ids 主键ID数组
     */
    void delete(Long[] ids);
    
    /**
     * 更新
     * @param serverDto 服务器信息表
     */
    void update(ServerDto serverDto);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 服务器信息表
     */
    ServerVo find(Long id);
    
    /**
     * 查询
     * @param pageNum,pageSize,serverDto 分页查询参数
     * @return 服务器信息表分页数据
     */
    IPage<ServerVo> query(int pageNum, int pageSize, String content,String affiliatedCompany);

    /**
     * 清空redis缓存
     */
    public Boolean clearRedisServerCache();

    /**
     * 批量关机
     * @param ids 主键ID
     */
    void batchPowerOff(Long[] ids);

    /**
     * 同步系统时间
     */
    void synchronizationTime(Long[] ids);


    /**
     * 批量关机
     * @param serverDto 服务器Dto
     */
    void powerOffDto(ServerDto serverDto);

    /**
     * 重启
     * @param id 主键ID
     */
    void restart(Long[] id);

    /**
     * 重启
     * @param serverDto 服务器Dto
     */
    void restartDto(ServerDto serverDto);

    /**
     * 测试连接
     * @param serverDto 服务器信息表
     */
    void testConnection(Long id);

    /**
     * 验证服务器账号密码
     * @param serverDto 服务器信息表
     */
    void testUser(ServerDto serverDto);

    /**
     * 服务器日志下拉框
     * @param id 服务器id
     */
    List<String> getLogInformation(Long id,String startTime,String endTime);


}
