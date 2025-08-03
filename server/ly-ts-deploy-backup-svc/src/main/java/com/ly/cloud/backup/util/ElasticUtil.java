package com.ly.cloud.backup.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.elasticsql.FullLinkSql;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ly.cloud.backup.common.constant.CommonConstant.NULL;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;
import static com.ly.cloud.backup.common.enums.TimeEnums.getValue;

/**
 * Class Name: DingServiceImpl Description:
 *
 * @author: liushaoyang
 * @mail: liushaoyang@ly-sky.com
 * @date: 2022年03月11日 11:20
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
public class ElasticUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticUtil.class);


    private static final String DATE_STRING = ":00";
    public static JSONObject queryDSL(String index, String queryDSL, RestHighLevelClient restHighLevelClient){
        JSONObject j=new JSONObject();
        try {
            Request request = new Request("POST", "/"+index+"/_search");
            request.setJsonEntity(queryDSL);
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(responseBody);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return j;
    }



    public static Map<String, String> getTime(int time){
        String timeNoUnit = Objects.requireNonNull(String.valueOf(getValue(time)));
        Map<String, String> times = new HashedMap<>(16);
        times = ElasticUtil.getMinute(UsualUtil.getInt(timeNoUnit));
//        times = ElasticUtil.getMinute((int)Double.parseDouble(timeNoUnit));
        return times;
    }

    /**
     * 返回amount分钟之前的开始时间和当前的结束时间
     *
     * @param amount 减的时间（分钟）
     * @return
     */
    public static Map<String, String> getMinute(int amount) {
        Map<String, String> res = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        res.put("end", sdf.format(date));
        Date start = dayAddAndSub(Calendar.MINUTE, amount);
        res.put("start", sdf.format(start));
        return res;
    }

    /**
     * 日期的加减方法
     * 用于在当前的天或者小时或者分钟或者月份的基础上加上或者减去若干小时，分钟，日，月
     *
     * @param currentDay 当前月份的某一天
     * @param day        (Calendar.DATE 天 Calendar.HOUR 小时 Calendar.MINUTE 分钟 Calendar.MONTH 月)需要加上的天数或者需要减去的天数，
     *                   例如：加上10天：(Calendar.DATE,10）减去十天：(Calendar.DATE,-10)
     * @param currentDay
     * @param day
     * @return 返回加上或者减去的那个日期
     * @return
     */
    public static Date dayAddAndSub(int currentDay, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(currentDay, -day);
        return calendar.getTime();
    }

    /**
     * 返回amount分钟之前的开始时间和当前的结束时间
     *
     * @param amount 减的时间（分钟）
     * @return
     */
    public static Map<String, String> getHour(int amount) {
        Map<String, String> res = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        res.put("end", sdf.format(date));
        Date start = dayAddAndSub(Calendar.HOUR, amount);
        res.put("start", sdf.format(start));
//        res.put("start", sdf.format(date.getTime() - (long) amount * 60000/60));
        return res;
    }

    /**
     * 计算百分位
     *
     * @param data
     * @param p    百分位 90% 传0.9
     * @return
     */
    public static double percentile(double[] data, double p) {
        int n = data.length;
        Arrays.sort(data);
        double px = p * (n - 1);
        int i = (int) java.lang.Math.floor(px);
        double g = px - i;
        if (g == 0) {
            return data[i];
        } else {
            return (1 - g) * data[i] + g * data[i + 1];
        }
    }

    /**
     * 取一个数组的百分位数
     *
     * @param array
     * @param percentile 百分位 90%传0.9
     * @param digit      保留的小数位
     * @return
     */
    public static String getPercentile(double[] array, double percentile, int digit) {
        if (array == null || array.length == 0) {
            return null;
        }
        if (array.length == 1) {
            return new BigDecimal(String.valueOf(array[0])).setScale(digit, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros().toPlainString();
        }
        Arrays.sort(array);
        BigDecimal x = new BigDecimal(array.length - 1).multiply(new BigDecimal(String.valueOf(percentile)));
        int i = x.intValue();
        BigDecimal j = x.subtract(new BigDecimal(i));
        BigDecimal r = (new BigDecimal(1).subtract(j)).multiply(new BigDecimal(String.valueOf(array[i]))).add(j.multiply(new BigDecimal(String.valueOf(array[i + 1]))));
        return r.setScale(digit, BigDecimal.ROUND_HALF_EVEN).stripTrailingZeros().toPlainString();
    }

    public static List<Map<String, Object>> sql(String sql, RestHighLevelClient restHighLevelClient) {
        List<Map<String, Object>> res = new ArrayList<>();
        try {
            Request request = new Request("POST", "/_sql?format=txt");
            request.setJsonEntity(sql);
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            String[] lines = responseBody.split("\n");
            List<String> names = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(Arrays.asList(lines))){
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    String[] tmp = line.split("\\|");
                    List<String> fields = Arrays.stream(tmp).map(String::trim).collect(Collectors.toList());
                    if (i == 0) {
                        names = fields;
                    } else if (i == 1) {
                        continue;
                    } else {
                        if (line.trim().length() == 0) {
                            continue;
                        }
                        Map<String, Object> map = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(names)){
                            for (int j = 0; j < names.size(); j++) {
                                String key = names.get(j);
                                String value="";
                                if (CollectionUtils.isNotEmpty(fields)&&fields.size()>j){
                                    value = fields.get(j);
                                }
                                if (StringUtils.isEmpty(UsualUtil.getString(value))){
                                    map.put(key, null);
                                }else {
                                    map.put(key, value);
                                }
                            }
                        }
                        res.add(map);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return res;
    }

    /*public static List<Map<String, Object>> sqlThrow(String sql, RestHighLevelClient restHighLevelClient) throws Exception {
        List<Map<String, Object>> res = new ArrayList<>();
            Request request = new Request("POST", "/_sql?format=txt");
            request.setJsonEntity(sql);
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            String[] lines = responseBody.split("\n");
            List<String> names = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(Arrays.asList(lines))){
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    String[] tmp = line.split("\\|");
                    List<String> fields = Arrays.stream(tmp).map(String::trim).collect(Collectors.toList());
                    if (i == 0) {
                        names = fields;
                    } else if (i == 1) {
                        continue;
                    } else {
                        if (line.trim().length() == 0) {
                            continue;
                        }
                        Map<String, Object> map = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(names)){
                            for (int j = 0; j < names.size(); j++) {
                                String key = names.get(j);
                                String value="";
                                if (CollectionUtils.isNotEmpty(fields)&&fields.size()>j){
                                    value = fields.get(j);
                                }
//                                if (Objects.equals(key, CommonConstant.TIMESTAMP_FIELD)) {
//                                    map.put(CommonConstant.TIMESTAMP_FIELD, simpleDateFormat.format(DATE_FORMAT_SPECIAL.parse(value)));
//                                } else {
                                map.put(key, value);
//                                }
                            }
                        }
                        res.add(map);
                    }
                }
            }
        return res;
    }*/


    public static Map<String, Object> sqlReturnMap(String sql, RestHighLevelClient restHighLevelClient)  {
        Map<String, Object> res = new HashedMap<>();
            Request request = new Request("POST", "/_sql?format=txt");
        try {
            request.setJsonEntity(sql);
            Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());
            String[] lines = responseBody.split("\n");
            List<String> names = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String[] tmp = line.split("\\|");
                List<String> fields = Arrays.stream(tmp).map(String::trim).collect(Collectors.toList());
                if (i == 0) {
                    names = fields;
                } else if (i == 1) {
                    continue;
                } else {
                    if (line.trim().length() == 0) {
                        continue;
                    }
                    Map<String, Object> map = new HashMap<>();
                    for (int j = 0; j < names.size(); j++) {
                        String key = names.get(j);
                        String value = "";
                        if (CollectionUtils.isNotEmpty(fields)&&fields.size()>j){
                            value = fields.get(j);
                        }
                        if (StringUtils.isEmpty(UsualUtil.getString(value))){
                            map.put(key, null);
                        }else {
                            map.put(key, value);
                        }
                    }
                    res = map;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return res;
    }

    public static HashMap<String,String> getSizeReturnMap(long size) {
        HashMap<String,String> resultHashMap = new HashMap<>();
        String unit = "GB";
        if (size<=0){
            resultHashMap.put("unit",unit);
            resultHashMap.put("byte","0");
            return resultHashMap;
        }
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i));
            unit = "GB";
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i));
            unit = "MB";
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i));
            unit = "KB";
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.toString();
                unit = "B";
            } else {
                bytes.append((int) size);
                unit = "B";
            }
        }
        resultHashMap.put("unit",unit);
        resultHashMap.put("byte",bytes.toString());
        return resultHashMap;
    }

    public static HashMap<String,String> getSizeReturnMb(long size) {
        HashMap<String, String> resultHashMap = new HashMap<String, String>();
        String unit = "MB";
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        double i = (size / (1024.0)/ (1024.0));
        bytes.append(format.format(i));
        unit = "MB";
        String byteString = bytes.toString();
        if (byteString.startsWith(".")){
            byteString = "0"+byteString;
        }
        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(byteString);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            byteString = String.valueOf((int) mainWastage);
        }
        resultHashMap.put("unit",unit);
        resultHashMap.put("byte",byteString);
        return resultHashMap;
    }

    public static String getSizeReturnKb(long size) {
        String unit = "KB";
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        double i = (size / (1024.0));
        bytes.append(format.format(i));
        unit = "KB";
        String byteString = bytes.toString();
        if (byteString.startsWith(".")){
            byteString = "0"+byteString;
        }
        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(byteString);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            byteString = String.valueOf((int) mainWastage);
        }
        return byteString;
    }

    public static String getSizeReturnGb(long size) {
        String unit = "GB";
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        double i = (size / (1024.0 * 1024.0 * 1024.0));
        bytes.append(format.format(i));
        unit = "GB";
        String byteString = bytes.toString();
       if (byteString.startsWith(".")){
           byteString = "0"+byteString;
       }
        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(byteString);
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            byteString = String.valueOf((int) mainWastage);
        }
        return byteString;
    }

    public static String getSize(long size) {
        String unit = "GB";
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
            unit = "GB";
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
            unit = "MB";
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
            unit = "KB";
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
                unit = "B";
            } else {
                bytes.append((int) size).append("B");
                unit = "B";
            }
        }
        return bytes.toString();
    }

    /**
     * 运算以MB为单位，返回值不带单位
     * @param size
     * @return
     */
    public static String getSizeToMbNoUnit(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 0) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i));
        }
        //前补零
        if (bytes.substring(0,1).equals(".")){
            return "0"+bytes.toString();
        }
        return bytes.toString();
    }

    /**
     * 运算以GB为单位，返回值不带单位
     * @param size
     * @return
     */
    public static String getSizeToGbNoUnit(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 0) {
            double i = (size / (1024.0 * 1024.0*1024.0));
            bytes.append(format.format(i));
        }
        //前补零
        if (bytes.substring(0,1).equals(".")){
            return "0"+bytes.toString();
        }
        return bytes.toString();
    }

    public static double getSizeToGB(String size) {
        double num=0;
        String unit="";
        if(Character.isDigit(size.charAt(size.length()-2))){
            num =Double.valueOf(size.substring(0,size.length()-1));
            unit=size.substring(size.length()-1,size.length());
        }else {
            num =Double.valueOf(size.substring(0,size.length()-2));
            unit=size.substring(size.length()-2,size.length());
        }
        if(unit.equals("MB")){
            num=num/1024;
        }else if(unit.equals("KB")){
            num=num/1024/1024;
        }else if(unit.equals("B")){
            num=num/1024/1024/1024;
        }
        return num;
    }

        public static String getRatio(double used, double total) {
        DecimalFormat format = new DecimalFormat(".00%");
        double ratio = used / total;
        return format.format(ratio);
    }

    public static String format(String pct) {
        DecimalFormat format = new DecimalFormat(".00%");
        return format.format(Double.parseDouble(pct));
    }

    /**
     * 微秒转毫秒
     *微秒转毫秒(1000us = 1ms)
     * long 默认值为0
     */
    public static long getUsToMs(long us){
        if (us >0L){
            if (us<1000L){
                return 1L;
            }
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
    public static String getLatencyUsToMs(long us){
        if (us >0L){
            long ms = us / 1000L;
            return ms+MS;
        }
        return "0";
    }

    /**
     * 科学计数法转换成普通计数法
     */
    public static String getScientificToNormalMethod(String scientificValue){
        if (StringUtils.isEmpty(scientificValue)){
            return scientificValue;
        }
        BigDecimal db = new BigDecimal(scientificValue);
        return db.toPlainString();
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
     * 不做四舍五入
     * keep:保留小数点后0位
     */
    public static String getNoRound(double value){
        try {
            int keep = 0;
            final DecimalFormat formater = new DecimalFormat();
            formater.setMaximumFractionDigits(keep);
            formater.setGroupingSize(0);
            formater.setRoundingMode(RoundingMode.FLOOR);
            String format = formater.format(value);
        //判断是否小于0，是则给0.01
        if (value >0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
            return format;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     * 不做四舍五入
     * keep:保留小数点后2位
     */
    public static String getNoRound(double value,int keep){
        try {
            //如果keep为null，默认保留2位小数
            if (keep <=0){
                keep = 2;
            }
            final DecimalFormat formater = new DecimalFormat();
            formater.setMaximumFractionDigits(keep);
            formater.setGroupingSize(0);
            formater.setRoundingMode(RoundingMode.FLOOR);
            String format = formater.format(value);
        //判断是否小于0，是则给0.01
        if (value >0 && Double.parseDouble(format) <=0){
            format = ZERO_POINT_ZERO_ONE;
        }
            return format;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    /**
     *
     * 舍弃小数位,取整数
     */
    public static long getDiscardDecimalPlaceReturnLong(Object value){
        long format = 0L;
        if (value == null || value.equals(NULL)){
            return format;
        }

        //则小数点后的数字都为0，就取整数
        double mainWastage = Double.parseDouble(value.toString());
        //判断是否符合取整条件
        if((int) mainWastage - mainWastage == 0){
            format = (long) mainWastage;
        }
        return format;
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
    public static String getPercentage(double value){
        //四舍五入，保留小数点后几位
        BigDecimal bigdecimal = new BigDecimal("100.0").multiply(new BigDecimal(value));
        return bigdecimal.doubleValue()+PERCENT;
      /*  value = value * 100;
       return value +PERCENT;*/
    }

    /**
     * 获取（百分率）
     */
    public static String getRateNoUnit(Object value){
        return getNoRound(Double.parseDouble(String.valueOf(value))*100,2);
    }

    /**
     * 获取（百分率）
     */
    public static String getRate(String value){
        return getNoRound(Double.parseDouble(String.valueOf(value))*100,2)+PERCENT;
    }

    /**
     * 获取错误率（百分率）
     */
    public static String getErrorRate(long requestTotal, long errorRequestCount){
        //计算错误率
        double errorRate = errorRequestCount *1.0 / requestTotal;
        //四舍五入，保留小数点后4位
        String errorRate4f = getRound(errorRate,2);
        //四舍五入，保留小数点后几位
        return getPercentage(Double.parseDouble(errorRate4f));
    }

    /**
     * 按每小时统计cpu运行的使用率(%)
     */
    public static List getCpuRate(String ip,Map<String, String> times,int time, RestHighLevelClient restHighLevelClient) throws ParseException {
        List resultList = new ArrayList<>();
        String cpuMetricbeatAvgSql = FullLinkSql.getCpuMetricbeatAvg(ip,times,time,times.get("start"),times.get("end"));
        logger.info("cpuMetricbeatAvgSql:"+cpuMetricbeatAvgSql);
        List<Map<String, Object>> cpuMetricbeatTableResult = ElasticUtil.sql(cpuMetricbeatAvgSql, restHighLevelClient);
        cpuMetricbeatTableResult.forEach(cpuMetricbeatTable->{
            List<String> tempList = new ArrayList<>();
            String timestamp = null;
            try {
                //获取时间
                timestamp = DateUtil.getIsoToSimpleDateFormat(cpuMetricbeatTable.get(TIMESTAMP).toString());
                tempList.add(timestamp);
                //获取使用量
                String use = ElasticUtil.format(UsualUtil.getString(cpuMetricbeatTable.get("avg(system.cpu.total.norm.pct)")));
                //去掉%号
                tempList.add(use.substring(0,use.length()-1));
                resultList.add(tempList);
            } catch (ParseException e) {
                logger.error(e.getMessage(),e);
            }
        });
        return resultList;
    }

    /**
     * 按每小时统计内存运行的使用情况（GB）
     */
    public static List getRamRate(String ip,Map<String, String> times,int time, RestHighLevelClient restHighLevelClient) throws ParseException {
        List resultList = new ArrayList<>();
        String ramMetricbeatAvgSql = FullLinkSql.getRamMetricbeatAvg(ip,times,time,times.get("start"),times.get("end"));
        logger.info("ramMetricbeatAvgSql:"+ramMetricbeatAvgSql);
        List<Map<String, Object>> ramMetricbeatAvgResultList = ElasticUtil.sql(ramMetricbeatAvgSql, restHighLevelClient);
        ramMetricbeatAvgResultList.forEach(ramMetricbeatAvgResult->{
            List<String> tempList = new ArrayList<>();
            String timestamp = null;
            try {
                //获取时间
                timestamp = DateUtil.getIsoToSimpleDateFormat(ramMetricbeatAvgResult.get(TIMESTAMP).toString());
                tempList.add(timestamp);
                //获取使用量
                long used = UsualUtil.getLong(getScientificToNormalMethod(UsualUtil.getString(ramMetricbeatAvgResult.get("avg(system.memory.actual.used.bytes)"))));
                String userToGb = ElasticUtil.getSizeReturnMap(used).get(BYTE);
                String use = getRound(UsualUtil.getDouble(userToGb), 2);
                tempList.add(use);
                resultList.add(tempList);
            } catch (ParseException e) {
                logger.error(e.getMessage(),e);
            }
        });
        return resultList;
    }

    /**
     * 按每小时统计网络运行的使用情况（GB）
     */
    public static List getNetworkRate(List<Map<String, Object>> cpuMetricbeatTableResult){
        List resultList = new ArrayList<>();
        HashMap<String, Object> resultHashMap = new HashMap<>(16);
        if (Optional.ofNullable(cpuMetricbeatTableResult).isPresent()){
            cpuMetricbeatTableResult.forEach(cpuMetricbeatTable->{
                String timestamp = null;
                try {
                    timestamp = DateUtil.getIsoToSimpleDateFormat(cpuMetricbeatTable.get("@timestamp").toString());
                } catch (ParseException e) {
                    logger.error(e.getMessage(),e);
                }
                //2022-05-09 02
                String subTimestamp = timestamp.substring(0, timestamp.indexOf(":"));
                if (CollectionUtils.isNotEmpty(cpuMetricbeatTable) && resultHashMap.containsKey(subTimestamp)){
                    String tableResultPercent = String.valueOf(cpuMetricbeatTable.get("total"));
                    String mapResultPercent = String.valueOf(resultHashMap.get(subTimestamp));
                    //先去掉GB，再做运算
                    double subTableResultPercent = Double.parseDouble(tableResultPercent.substring(0, tableResultPercent.length() - 2));
                    double subMapResultPercent = Double.parseDouble(mapResultPercent.substring(0, mapResultPercent.length() - 2));
                    String roundTpm = getRound((subTableResultPercent + subMapResultPercent) / 2, 2);
                    resultHashMap.put(subTimestamp,roundTpm+GB);
                }else {
                    resultHashMap.put(subTimestamp,cpuMetricbeatTable.get("total"));
                }
            });
            if (CollectionUtils.isNotEmpty(resultHashMap)){
                resultHashMap.forEach((k,v)->{
                    List<String> tempList = new ArrayList<>();
                    tempList.add(k+DATE_STRING);
                    //去掉GB
                    tempList.add(v.toString().substring(0, v.toString().length() - 2));
                    resultList.add(tempList);
                });
            }
        }
        return resultList;
    }

    /**
     * 按每小时统计磁盘运行的使用情况（GB）
     */
    public static List getFileRate(String ip,Map<String, String> times,int time, RestHighLevelClient restHighLevelClient) throws ParseException {
        List resultList = new ArrayList<>();
        String systemDiskAvgSql = FullLinkSql.getSystemDiskAvg(ip,times,time,times.get("start"),times.get("end"));
        logger.info("systemDiskAvgSql:"+systemDiskAvgSql);
        List<Map<String, Object>> cpuMetricbeatTableResult = ElasticUtil.sql(systemDiskAvgSql, restHighLevelClient);
        cpuMetricbeatTableResult.forEach(cpuMetricbeatTable->{
            List<String> tempList = new ArrayList<>();
            String timestamp = null;
            try {
                //获取时间
                timestamp = DateUtil.getIsoToSimpleDateFormat(cpuMetricbeatTable.get(TIMESTAMP).toString());
                tempList.add(timestamp);
                //获取使用量
                long fsUsedSize = UsualUtil.getLong(cpuMetricbeatTable.get("avg(system.fsstat.total_size.used)"));
                String userToGb = ElasticUtil.getSizeReturnMap(fsUsedSize).get(BYTE);
                String use = getRound(UsualUtil.getDouble(userToGb), 2);
                tempList.add(use);
                resultList.add(tempList);
            } catch (ParseException e) {
                logger.error(e.getMessage(),e);
            }
        });
        return resultList;
    }

    /**
     * 数组去空去重
     */
    public static List getNonNullDistinctList(List<Map<String, Object>> list){
        if (CollectionUtils.isEmpty(list)){
            return list;
        }
        return  list.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }




}
