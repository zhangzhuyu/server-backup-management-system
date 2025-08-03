package com.ly.cloud.backup.scheduled;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduledTaskUtil {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskUtil.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    /**
     * cron：使用Cron表达式。　每分钟的1，2秒运行
     */
    /**
     * 每月6日15时50分触发
     * @Scheduled(cron = "0 50 15 6 * ? ")
     */
    //秒/分/时/日/月/年

    /**
     * 从1日开始,每5天执行一次16:16分运行
     *
     * @Scheduled(cron = "0 16 16 1/5 1/1 ? ")
     */
    @Scheduled(cron = "0 16 16 1/5 1/1 ? ")
//        @Scheduled(cron = "0 50 15 6 * ? ")
    public void reportCurrentTimeWithCronExpression() {
        log.info("11111111111Cron Expression: The time is now {}", dateFormat.format(new Date()));
    }


    /*        *
     * fixedRate：固定速率执行。每5秒执行一次。*/

    /*    @Scheduled(fixedRate = 10000)
        public void reportCurrentTimeWithFixedRate() {
            log.info("2222222222Current Thread : {}", Thread.currentThread().getName());
            log.info("222222222Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
        }*/

    /*  *//**
     * fixedDelay：固定延迟执行。距离上一次调用成功后2秒才执。
     *//*
        @Scheduled(fixedDelay = 2000)
        public void reportCurrentTimeWithFixedDelay() {
            try {
                TimeUnit.SECONDS.sleep(3);
                log.info("Fixed Delay Task : The time is now {}", dateFormat.format(new Date()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * initialDelay:初始延迟。任务的第一次执行将延迟5秒，然后将以5秒的固定间隔执行。
         */
      /*  @Scheduled(initialDelay = 1000, fixedRate = 5000)
        public void reportCurrentTimeWithInitialDelay() {
            log.info("Fixed Rate Task with Initial Delay : The time is now {}", dateFormat.format(new Date()));
        }*/


}

