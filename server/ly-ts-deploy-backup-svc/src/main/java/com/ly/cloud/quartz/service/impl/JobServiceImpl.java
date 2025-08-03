package com.ly.cloud.quartz.service.impl;




import com.ly.cloud.quartz.constants.CommonConstant;
import com.ly.cloud.quartz.entity.JobAndTrigger;
import com.ly.cloud.quartz.entity.TaskInfo;
import com.ly.cloud.quartz.mapper.JobAndTriggerMapper;
import com.ly.cloud.quartz.service.IJobService;
import com.ly.cloud.quartz.singleton.SystemCache;
import com.ly.cloud.quartz.vo.Result;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class JobServiceImpl implements IJobService {

    private static Logger log = LoggerFactory.getLogger(JobServiceImpl.class);
    @Autowired
    private Scheduler scheduler;

    @Resource
    private JobAndTriggerMapper jobAndTriggerMapper;


    @Override
    public Result getJobAndTriggerList(int pageNum, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        try {
//            Page<LoginLogVo> page = new Page<>(pageNum > 0 ? pageNum : 1, pageSize > 0 ? pageSize : 15);
//            PageHelper.startPage(pageNum, pageSize);
            List<JobAndTrigger> list = jobAndTriggerMapper.getJobAndTriggerDetails();
//            PageInfo<JobAndTrigger> page = new PageInfo<JobAndTrigger>(list);
            List<TaskInfo> taskInfoList = (List<TaskInfo>) SystemCache.getInstance().getCacheMap().get(CommonConstant.TASK_CACHE_KEY);
            map.put("JobAndTriggerList",list);
            map.put("number", list.size());
            map.put("taskList", taskInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            Result.FAIL("查询失败");
        }
        return Result.OK(map);
    }

    @Override
    public Result createJobAndTrigger(String jobClassName, String jobName, String jobGroupName,String cronExpression) {

        //构建job信息
        JobDetail jobDetail;
        try {

            jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass()).withIdentity(jobName, jobGroupName).build();

            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(jobName, jobGroupName)
                    .withSchedule(scheduleBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);

            return Result.OK();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建定时任务失败 ", e);
            return Result.FAIL("创建定时任务失败");
        }
    }


    @Override
    public Result pauseJob(String jobName, String jobGroupName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
            return Result.OK();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("暂停定时任务失败", e);
            return Result.FAIL("暂停定时任务失败");

        }
    }

    @Override
    public Result resumeJob(String jobClassName, String jobGroupName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
            return Result.OK();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("恢复定时任务失败", e);
            return Result.FAIL("恢复定时任务失败");
        }
    }

    @Override
    public Result rescheduleJob(String jobName, String jobGroupName, String cronExpression) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
            CronTrigger cronTrigger = rebulidTriggerKey(jobName, jobGroupName, cronExpression);
            scheduler.rescheduleJob(triggerKey, cronTrigger);
            return Result.OK();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("更新定时任务失败", e);
            return Result.FAIL("更新定时任务失败");
        }
    }

    @Override
    public Result deleteJob(String jobClassName, String jobGroupName) {

        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
            return Result.OK();
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("删除定时任务失败", e);
            return Result.FAIL("删除定时任务失败");
        }

    }

    @Override
    public Result triggerJobAtOnce(String jobName, String jobGroupName) {
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        try {
            scheduler.triggerJob(jobKey);
            return Result.OK();
        } catch (SchedulerException e) {
            log.error("立即执行任务时发生异常", e);
            return Result.FAIL("立即执行任务时发生异常");

        }
    }

    @Override
    public Result validateJobNameAndGroupName(String jobName, String jobGroupName) {
        return Result.OK(jobAndTriggerMapper.queryJobByNameAndGroupName(jobName, jobGroupName) == 0);
    }


    private Job getClass(String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        return (Job) clazz.newInstance();
    }

    private CronTrigger rebulidTriggerKey(String jobName, String jobGroupName, String cronExpression) {

        //重置crontrigger
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        CronTrigger trigger = null;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        return trigger;
    }

}