package com.ly.cloud.backup.mapper;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.cloud.backup.common.handler.batchandle.CustomMapper;
import com.ly.cloud.backup.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ly.cloud.backup.dto.WorkOrderDto;
import com.ly.cloud.backup.po.WorkOrderPo;

/**
 * 异常工单信息表
 *
 * @author chenguoqing
 */
@Mapper
public interface WorkOrderMapper extends CustomMapper<WorkOrderPo> {

    /**
     * 条件查询异常工单信息
     *
     * @param page         : 分页辅助页
     * @param workOrderDto : 条件
     * @return IPage<WorkOrderVo> ：异常工单信息
     * @author: jiangzhongxin
     * @date: 2022/3/17 10:40
     */
    public IPage<WorkOrderVo> list(@Param("page") Page<WorkOrderVo> page, @Param("dto") WorkOrderDto workOrderDto);

    /**
     * 查询status处理任务ids
     *
     * @param taskIdList : 工单任务id集合
     * @param status     : 处理状态
     * @return List<Integer> ： status处理任务ids
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:12
     */
    public List<Integer> getProcessedStatusIds(@Param("list") List<String> taskIdList, @Param("status") String status);

    /**
     * 批量更新工单数据
     *
     * @param taskIdList : 工单任务id集合
     * @param result     : 结果反馈
     * @param status     : 处理状态
     * @param handleTime : 处理完成时间
     * @return Integer : 更新数量
     * @author: jiangzhongxin
     * @date: 2022/3/16 15:15
     */
    public Integer batchTicketPoData(@Param("list") List<String> taskIdList, @Param("result") String result, @Param("status") String status, @Param("date") Date handleTime);

    /**
     * 单个查询异常工单详情数据
     *
     * @param id : 工单id
     * @return WorkOrderVo : 异常工单详情数据
     * @author: jiangzhongxin
     * @date: 2022/3/17 14:43
     */
    public WorkOrderVo detail(@Param("id") String id);


    public List<WorkOrderVo> getOrderListByStatus(@Param("status") String status);

    /**
     * 批量修改每个处理状态的工单信息
     * @param workOrderDto
     * @return
     */
    int updateStatusBath(@Param("dto") WorkOrderDto workOrderDto, @Param("currentDate") Date date);
}