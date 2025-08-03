package com.ly.cloud.quartz.service;


import com.ly.cloud.quartz.vo.Result;

;

public interface IJobService {

    /**
     *分页查询任务信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result getJobAndTriggerList(int pageNum, int pageSize);

    /**
     * 创建定时任务
     * @param jobClassName
     * @param jobName
     * @param jobGroupName
     * @return
     */
    Result createJobAndTrigger(String jobClassName, String jobName, String jobGroupName, String cronExpression);

    /**
     * 暂停执行定时任务
     * @param jobClassName
     * @param jobGroupName
     * @return
     */
    Result pauseJob(String jobClassName, String jobGroupName);

    /**
     * 恢复定时任务
     * @param jobClassName
     * @param jobGroupName
     * @return
     */
    Result resumeJob(String jobClassName, String jobGroupName);

    /**
     * 修改定时任务
     * @param jobName
     * @param jobGroupName
     * @param cronExpression
     */
    Result rescheduleJob(String jobName, String jobGroupName, String cronExpression);

    /**
     * 删除定时任务
     * @param jobClassName
     * @param jobGroupName
     * @return
     */
    Result deleteJob(String jobClassName, String jobGroupName);

    /**
     * 立即执行定时任务
     * @param jobName
     * @param jobGroupName
     * @return
     */
    Result triggerJobAtOnce(String jobName, String jobGroupName);

    /**
     * 校验任务名称是否重复
     * @param jobName
     * @param jobGroupName
     * @return
     */
    Result validateJobNameAndGroupName(String jobName, String jobGroupName);


}
