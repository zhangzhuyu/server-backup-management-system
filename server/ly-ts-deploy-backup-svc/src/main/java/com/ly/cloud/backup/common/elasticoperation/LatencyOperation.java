



package com.ly.cloud.backup.common.elasticoperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.MS;

/**
 * SYC
 * 应用运算工具类-延迟
 */
public class LatencyOperation {

    private static final Logger logger = LoggerFactory.getLogger(LatencyOperation.class);

    /**
     * 微秒转毫秒
     *微秒转毫秒(1000us = 1ms)
     * long 默认值为0
     */
    public static String getLatencyUsToMs(long us){
        if (us >0L){
            long ms = us / 1000L;
            return ms+MS;
        }
        return "0";
    }


}
