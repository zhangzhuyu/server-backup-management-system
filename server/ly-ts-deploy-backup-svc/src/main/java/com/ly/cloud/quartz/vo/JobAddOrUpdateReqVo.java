package com.ly.cloud.quartz.vo;

import lombok.Data;

/**
 * @description:
 * @author: ljb
 * @date: 2022 5 27
 */
@Data
public class JobAddOrUpdateReqVo {


    private String jobClassName;
    private String jobName;
    private String jobGroupName;
    private String cronExpression;

}
