



package com.ly.cloud.backup.common.elasticoperation;

import com.ly.cloud.backup.util.UsualUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;

/**
 * SYC
 * 应用运算工具类-错误率
 */
public class ErrorRateOperation {

    private static final Logger logger = LoggerFactory.getLogger(ErrorRateOperation.class);


    /**
     * 获取错误率（百分率）
     */
    public static String getErrorRate(long requestTotal, long errorRequestCount,int avg){
        //计算错误率
        if (errorRequestCount>0){
            double errorRate = errorRequestCount / requestTotal;
            //四舍五入，保留小数点后4位
            String errorRate4f = TpmOperation.getRound(errorRate,2);
            //四舍五入，保留小数点后几位
            double aDouble = UsualUtil.getDouble(getPercentageNoUnit(Double.parseDouble(errorRate4f)));
//            double aDouble = UsualUtil.getDouble(getPercentage(Double.parseDouble(errorRate4f)));
            if (aDouble>0){
                return UsualUtil.getString(UsualUtil.getDouble(getPercentageNoUnit(Double.parseDouble(errorRate4f)))/avg);
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
        String errorRate4f = TpmOperation.getRound(errorRate,2);
        //四舍五入，保留小数点后几位
        return getPercentage(Double.parseDouble(errorRate4f));
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


}
