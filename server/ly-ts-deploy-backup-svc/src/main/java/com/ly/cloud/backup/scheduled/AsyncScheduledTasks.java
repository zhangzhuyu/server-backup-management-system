package com.ly.cloud.backup.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: SYC
 */
@Component
@EnableAsync
public class AsyncScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(AsyncScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private List<Integer> index = Arrays.asList(6, 6, 2, 3);
    int i = 0;

    /**
     * 定时周期性cron表达式执行任务
     */
//    @Scheduled(cron="* 29 15 * 6 ? ")
//    @Scheduled(cron="*/5 * * * * MON-FRI")
    public void doSomething() {
        // something that should execute on weekdays only
        System.out.println("任务调度执行时间到："+new Date());
    }

   /* @Scheduled(fixedRate = 5000)
    public void reportCurrentTimeWithFixedRate() {
        log.info("Current Thread : {}", Thread.currentThread().getName());

        if (i == 0) {
            log.info("Start time is {}", dateFormat.format(new Date()));
        }
        if (i < 5) {
            try {
                TimeUnit.SECONDS.sleep(index.get(i));
                log.info("Fixed Rate Task : The time is now {}", dateFormat.format(new Date()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }*/

    /**
     * fixedDelay：固定延迟执行。距离上一次调用成功后2秒才执。
     */
    //@Async
  /*  @Scheduled(fixedDelay = 2000)
    public void reportCurrentTimeWithFixedDelay() {
        try {
            TimeUnit.SECONDS.sleep(3);
            log.info("Fixed Delay Task : The time is now {}", dateFormat.format(new Date()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/

}
