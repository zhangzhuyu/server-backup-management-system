package com.ly.cloud.quartz.listener;


import com.ly.cloud.quartz.annotation.TaskNode;
import com.ly.cloud.quartz.constants.CommonConstant;
import com.ly.cloud.quartz.entity.TaskInfo;
import com.ly.cloud.quartz.singleton.SystemCache;
import com.ly.cloud.quartz.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: ljb
 * @date:
 */
@Component
public class AppStaredListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //获取系统缓存
        Map<String, Object> cacheMap = SystemCache.getInstance().getCacheMap();
        //获取task包下的所有任务
        Set<Class<?>> taskSet = ClassUtil.getClasses("com.ly.cloud.quartz.task");
        List<TaskInfo> taskInfoList = new ArrayList<>();
        taskSet.stream().forEach(t -> {
            TaskNode taskNode = t.getAnnotation(TaskNode.class);
            if (taskNode != null) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskClassName(t.getName());
                taskInfo.setTaskName(taskNode.taskName());
                taskInfoList.add(taskInfo);
            }
        });
        //将定时任务信息放入缓存中
        cacheMap.put(CommonConstant.TASK_CACHE_KEY, taskInfoList);

    }
}
