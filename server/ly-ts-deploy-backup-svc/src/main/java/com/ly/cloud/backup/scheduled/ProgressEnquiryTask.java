package com.ly.cloud.backup.scheduled;

import com.ly.cloud.backup.config.RedisConf;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.TimerTask;

import static com.ly.cloud.backup.common.constant.RedisConstants.*;

public class ProgressEnquiryTask extends TimerTask {

    private RedisConf redisUtil;
    public void setRedisUtil(RedisConf redisUtil) {
        this.redisUtil = redisUtil;
    }

    private String md5;
    private InputStream inputStream;
    private Integer total;
    public void setInputStream(InputStream inputStream){
        this.inputStream = inputStream;
    }
    public void setTotal(Integer total){
        this.total = total;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public void run() {
        try {
            if (inputStream.available()>0) {
                double d1 = total * 1.0;
                double d2 = inputStream.available() * 1.0;
                NumberFormat percentInstance = NumberFormat.getPercentInstance();
                Double d = ((d1 - d2) / d1) * 100;
                DecimalFormat format = new DecimalFormat("#0.0");
                String result = format.format(d);
                System.out.println("loading:========>"+result);
                // 放入缓存
                String key = REDIS_KEY_PREFIX_TC + "log-dump" + COLON + md5;
                long min = 1;
                long max = 2;
                long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
                redisUtil.set(key, result, TIME_OF_MINUTE+ rangeLong);
            }
            if (!(inputStream.available() >0)) {
                System.out.println("inputStream.available()==0====over========>");
            }
        } catch (Exception e) {
            System.out.println("=======exception=======");
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
