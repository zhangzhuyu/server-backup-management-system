package com.ly.cloud.quartz.entity;

import lombok.Data;

/**
 * @description:
 * @author: ljb
 * @date: 2022 5 27
 */
@Data
public class JobAndTrigger {

	private String jobName;

	private String jobGroup;

	private String jobClassName;

	private String triggerName;

	private String triggerGroup;

	private String cronExpression;

	private String triggerState;

	private String prevFireTime;

    private String nextFireTime;
}
