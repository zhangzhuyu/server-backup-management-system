package com.ly.cloud.quartz.mapper;


import com.ly.cloud.quartz.entity.JobAndTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JobAndTriggerMapper {

    List<JobAndTrigger> getJobAndTriggerDetails();

    JobAndTrigger getJobAndTriggerDetails(@Param("jobName") String jobName, @Param("jobGroupName") String jobGroupName);

    void updateTriggerPreTriggerTime(Long time);


    Integer queryJobByNameAndGroupName(@Param("jobName") String jobName, @Param("jobGroupName") String jobGroupName);
}
