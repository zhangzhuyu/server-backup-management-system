



package com.ly.cloud.backup.common.elasticoperation;

import com.ly.cloud.backup.util.UsualUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;

/**
 * SYC
 * 应用运算工具类
 */
public class AppliationOperation {

    private static final Logger logger = LoggerFactory.getLogger(AppliationOperation.class);


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
            format= UsualUtil.getString(UsualUtil.getDouble(format)/avg);
            return String.format("%." + keep + "f", UsualUtil.getDouble(format))+TPM;
        }
        format = UsualUtil.getString(UsualUtil.getDouble(format));
        return String.format("%." + keep + "f",  UsualUtil.getDouble(format))+TPM;
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
     * 获取错误率（百分率）
     */
    public static String getErrorRate(long requestTotal, long errorRequestCount,int avg){
        //计算错误率
        if (errorRequestCount>1 && requestTotal>1){
            double errorRate = errorRequestCount * 1.0 / requestTotal;
            //四舍五入，保留小数点后4位
            String errorRate4f = getRound(errorRate,2);
            //四舍五入，保留小数点后几位
            double aDouble = UsualUtil.getDouble(getPercentageNoUnit(Double.parseDouble(errorRate4f)));
            if (aDouble>0){
                return UsualUtil.getString(UsualUtil.getDouble(getPercentageNoUnit(Double.parseDouble(errorRate4f)))/avg)+PERCENT;
            }
            return getPercentage(Double.parseDouble(errorRate4f));
        }
     return "0"+PERCENT;
    }

    /**
     * 获取错误率（百分率）
     */
    public static String getErrorRate(long requestTotal, long errorRequestCount){
        //计算错误率
        double errorRate = errorRequestCount / requestTotal;
        //四舍五入，保留小数点后4位
        String errorRate4f = getRound(errorRate,2);
        //四舍五入，保留小数点后几位
        return getPercentage(Double.parseDouble(errorRate4f));
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
     * 转换为百分率，
     */
    public static String getPercentageNoUnit(double value){
        //四舍五入，保留小数点后几位
        value = value * 100;
        return String.valueOf(value);
    }

    /**
     * 转换为百分率，
     */
    public static String getPercentage(double value){
        //四舍五入，保留小数点后几位
        value = value * 100;
        return value +PERCENT;
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
     * @param us 微秒
     * @param avg 平均数
     * @return
     */
    public static long getUsToMs(long us,int avg){
        if (us >0L && avg>0){
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


}
