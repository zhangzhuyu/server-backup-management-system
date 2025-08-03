package com.ly.cloud.backup.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.common.handler.batchandle.CustomMapper;
import com.ly.cloud.backup.po.WarningObjectPo;
import com.ly.cloud.backup.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.WarningRuleDto;
import com.ly.cloud.backup.po.WarningRulePo;

/**
 * 告警规则信息表
 * @author chenguoqing
 *
 */
@Mapper
public interface WarningRuleMapper extends CustomMapper<WarningRulePo> {

    /**
     * 条件查询告警规则信息
     *
     * @author: jiangzhongxin
     * @date: 2022/3/17 10:40
     * @param page : 分页辅助页
     * @param warningRuleDto : 条件
     * @return: IPage<WarningRecordVo> ：告警规则信息
     */
    public IPage<WarningRuleListVo> list(@Param("page") Page<WarningRuleListVo> page, @Param("dto") WarningRuleDto warningRuleDto);

    public List<String> getIdList();
    /**
     * 单个查询告警规则信信息
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:12
     * @param id : 规则id
     * @return: WarningRuleListVo ：告警规则信息
     */
    public WarningRuleListVo get(@Param("id") String id);

    /**
     * 单个查询告警规则信息
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:12
     * @param id : 规则id
     * @param status : 是否启用 0 或 1
     * @return: Integer ：操作记录数
     */
    public Integer enable(@Param("id") String id, @Param("status") String status);

    /**
     * 单个查询告警规则信息
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:12
     * @param id : 规则id
     * @param status : 是否启用 0 或 1
     * @return: Integer ：操作记录数
     */
    public Integer enableRuleAndTaskById(@Param("id") String id, @Param("status") String status,@Param("param") String param);

    /**
     *  插入订阅内容
     * @param list
     * @return
     */
    public Integer insertPoBatchObject(@Param("list") List<WarningObjectPo> list);

    /**
     * 查询订阅内容
     * @param id
     * @return
     */
    public List<WarningObjectPo> getWarningObject(@Param("ruleId") String id);
    /**
     * 查询所有主机资源
     *
     * @author: jiangzhongxin
     * @date: 2022/3/21 10:37
     * @return: List<SelectVo> ：主机资源
     */
    public List<SelectVo> getAllServer();

    /**
     * 查询所有数据库资源
     *
     * @author: jiangzhongxin
     * @date: 2022/3/21 10:37
     * @return: List<SelectVo> ：数据库资源
     */
    public List<SelectVo> getAllDatabase();

    /**
     * 查询所有服务资源
     *
     * @author: jiangzhongxin
     * @date: 2022/3/21 10:37
     * @return: List<SelectVo> ：服务资源
     */
    public List<SelectVo> getAllService();

    /**
     * 查询所有中间件资源
     */
    public List<SelectVo> getAllMiddleware();

    /**
     * 单个查询告警规则目标list
     *
     * @author: ljb
     * @date: 2022/5/16 15:12
     * @param id : 规则id
     * @return: List(String) ：告警规则信息
     */
    public List<String> getWarningObjectList(@Param("id") Long id);


    /**
     * 查询所有指标下拉框
     *
     * @author: ljb
     * @date: 2022/3/21 10:37
     * @return: List<SelectVo> ：指标资源
     */
    public List<TargetSelectVo> getAllTarget();

    /**
     * 查询指标表
     * @return
     */
    public List<TargetVo> getTargetList();


    public String  getSqlLineById (@Param("id") Long id);

    public String  getTargetNameById (@Param("id") Long id);

    public int deleteWarningObjectById(@Param("ruleId") String ruleId);

    public int deleteRecordById(@Param("ruleId") String ruleId);

    public int deleteWorkOrderById(@Param("ruleId") String ruleId);

    public int deleteRecordObjectById(@Param("ruleId") String ruleId);

    /**
     * 统计每个预警等级下的规则数
     * @return
     */
    List<LevelRuleNumerVo> selectLevelTotal();

    /**
     * 根据预警对象id查出预警规则id
     * @param id
     * @return
     */
    List<String> selectRuleIdByResourcesObject(@Param("warningObject") Long id);

    List<String> selectDisableRuleId(@Param("list") List<String> ruleIds);

    void updateDisableByRuleIds(@Param("list") List<String> ids);

    void deleteWarningObjectByObjectId(@Param("objectId") Long id);

    List<WarningSubMethodVo> getHostWarningTotal(@Param("ip") String ip, @Param("startTime" )String startTime, @Param("endTime") String endTime);

    List<OrcalTotalVo> getOrcalWarningTotal(@Param("ip") String ip, @Param("port") String port, @Param("startTime" )String startTime, @Param("endTime") String endTime);

    List<OrcalTotalVo> getResourcesTotal();
}