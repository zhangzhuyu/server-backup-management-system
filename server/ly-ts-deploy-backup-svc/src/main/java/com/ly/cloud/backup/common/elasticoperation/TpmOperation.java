



package com.ly.cloud.backup.common.elasticoperation;

import com.ly.cloud.backup.util.UsualUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;

/**
 * SYC
 * 应用运算工具类-吞吐量
 */
public class TpmOperation {

    private static final Logger logger = LoggerFactory.getLogger(TpmOperation.class);

    /**
     * 吞吐量TPM：四舍五入
     * keep:保留小数点后4位
     */
    public static String getRoundTpm(double value,int keep,int avg){
        //如果keep为null，默认保留2位小数
        if (keep <=0){
            keep = 2;
        }
        //四舍五入，保留小数点后几位
        String format = String.format("%." + keep + "f", value);

        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(format);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            format = String.valueOf((int) mainWastage);
        }
        //判断是否小于0，是则给0.01
        if (value >0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
        if (UsualUtil.getDouble(format)>0){
            return UsualUtil.getString(UsualUtil.getDouble(format)/avg)+TPM;
        }
      return UsualUtil.getString(UsualUtil.getDouble(format))+TPM;
    }

    /**
     * 吞吐量TPM：四舍五入
     * keep:保留小数点后4位
     */
    public static String getRoundTpm(double value,int keep){
        //如果keep为null，默认保留2位小数
        if (keep <=0){
            keep = 2;
        }
        //四舍五入，保留小数点后几位
        String format = String.format("%." + keep + "f", value);

        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(format);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            format = String.valueOf((int) mainWastage);
        }
        //判断是否小于0，是则给0.01
        if (value >0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
        return format+TPM;
    }

    /**
     * 四舍五入
     * keep:保留小数点后4位
     */
    public static String getRound(double value,int keep,int avg){
        //如果keep为null，默认保留2位小数
        if (keep <=0){
            keep = 2;
        }
        //四舍五入，保留小数点后几位
        String format = String.format("%." + keep + "f", value);
        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(format);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            format = String.valueOf((int) mainWastage);
        }
        //判断是否小于0，是则给0.01
        if (value>0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
        if (UsualUtil.getDouble(format)>0){
            //求平均数
            return UsualUtil.getString(UsualUtil.getDouble(format)/avg);
        }
        //求平均数
        return UsualUtil.getString(UsualUtil.getDouble(format));
    }

    /**
     * 四舍五入
     * keep:保留小数点后4位
     */
    public static String getRound(double value,int keep){
        //如果keep为null，默认保留2位小数
        if (keep <=0){
            keep = 2;
        }
        //四舍五入，保留小数点后几位
        String format = String.format("%." + keep + "f", value);
        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(format);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            format = String.valueOf((int) mainWastage);
        }
        //判断是否小于0，是则给0.01
        if (value>0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
        return format;
    }

    /**
     * 微秒转毫秒
     *微秒转毫秒(1000us = 1ms)
     * long 默认值为0
     */
    public static long getUsToMs(long us){
        if (us >0L){
            long ms = us / 1000L;
            return ms;
        }
        return 0;
    }

    /**
     * 微秒转毫秒
     *微秒转毫秒(1000us = 1ms)
     * long 默认值为0
     */
    public static long getUsToMs(long us,int avg){
        if (us >0L){
            double ms = us / 1000d;
            double v = ms / avg;
            //判断是否小于1,
            if (v<1){
                return 1;
            }
            return UsualUtil.getLong(v);
        }
        return 0;
    }


    /**
     * 微秒转毫秒
     *微秒转毫秒(1000us = 1ms)
     * long 默认值为0
     */
    public static long getMsToUs(long us){
        if (us >0L){
            if (us<1000L){
                return 1L;
            }
            long ms = us / 1000L;
            return ms;
        }
        return 0;
    }

}
