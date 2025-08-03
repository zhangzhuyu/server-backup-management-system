package com.ly.cloud.backup.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ly.cloud.backup.common.constant.CommonConstant.*;
import static com.ly.cloud.backup.common.constant.FullLnkTraceConstant.*;
import static com.ly.cloud.backup.common.enums.TimeEnums.CUSTOM_TIME;

/**
 * 日期处理工具类
 */
public class DateUtil {

    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_MONTH = "yyyy-MM";
    public static final String DATE_FORMAT_YEAR = "yyyy";
    public static final String DATE_FORMAT_SPECIAL = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'";
    public static final String WAVE_SIGN = " ~ ";


    /**
     * 获取当前时间戳
     * @return
     */
    public static String getCurrentTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now()).toString();
    }

    public static Date getChinaTime(){
        //取得指定时区的时间：　　　　　　
        TimeZone zone = TimeZone.getTimeZone("GMT-8:00");
        Calendar cal = Calendar.getInstance(zone);
        return cal.getTime();
    }

    /**
     * 判断日期是否为当天
     */

    public static boolean isToday(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DAY);
        String param = sdf.format(time);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    /**
     * 时间格式转换(utc=>普通时间) 但utc世界标准时间与东8区时间相差8个小时，需要+8H
     */
    public static String getIsoToSimpleDateFormat(String iso) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_STR);
        Date parse = simpleDateFormat.parse(iso);
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR);
        String format1 = format.format(parse);
        return format1;
    }

    /**
     * 时间格式转换(普通时间=>utc) 但utc世界标准时间与东8区时间相差8个小时，需要-8H
     */
    public static String getSimpleToDateIsoFormat(String iso) throws ParseException {
        //如果已经是（2023-04-17T15:45:48）ISO格式就不做转换
        if (StringUtils.isNotEmpty(iso) && iso.toUpperCase(Locale.ROOT).contains("T")){
            return iso;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR);
        Date parse = simpleDateFormat.parse(iso);
        SimpleDateFormat format = new SimpleDateFormat(ISO_DATE_FORMAT_STR);
        return format.format(parse);
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
     * 时间格式转换(utc=>普通时间) 但utc世界标准时间与东8区时间相差8个小时，需要+8H
     */
    public static String getUtcToSimpleDateFormat(String utc) throws ParseException {
        DateFormat outputFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR, Locale.US);
        DateFormat inputFormat = new SimpleDateFormat(UTC_DATE_FORMAT_STR, Locale.US);
        Date date =inputFormat.parse(String.valueOf(utc));
        long time = 8*60*60*1000;//8h
        Date afterDate = new Date(date.getTime() + time);//+8h后的时间
        return outputFormat.format(afterDate);
    }

    /**
     * 获取当前时间(date格式)
     *
     * @return
     */
    public static Date getCurrentDate() throws ParseException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR);
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return format.parse(format.format(date));
    }

    /**
     * 获取当前时间字符串格式（年-月 yyyy-mm）
     *
     * @return
     */
    public static String getCurrentStringMonth() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_MONTH_FORMAT_STR);
        return format.format(date);
    }

    /**
     * 获取当前时间字符串格式
     *
     * @return
     */
    public static String getCurrentStringDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR);
        return format.format(date);
    }

    /**
     * date转字符串格式
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR).format(date);
    }

    /**
     * 开始时间与结束时间转字符串格式（yyyy-MM-dd HH:mm:ss ~ yyyy-MM-dd HH:mm:ss）
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间字符串（yyyy-MM-dd HH:mm:ss ~ yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDate(Date start, Date end) {
        return formatDate(start, end, WAVE_SIGN, SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 开始时间与结束时间转字符串格式（yyyy-MM-dd HH:mm:ss【separator】yyyy-MM-dd HH:mm:ss）
     *
     * @param start     开始时间
     * @param end       结束时间
     * @param separator 分隔符
     * @return 时间字符串（yyyy-MM-dd HH:mm:ss【separator】yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDate(Date start, Date end, String separator) {
        return formatDate(start, end, separator, SIMPLE_DATE_FORMAT_STR);
    }

    /**
     * 开始时间与结束时间转字符串格式（【format】【separator】【format】）
     *
     * @param start     开始时间
     * @param end       结束时间
     * @param separator 分隔符
     * @param format    格式
     * @return 时间字符串（【format】【separator】【format】）
     */
    public static String formatDate(Date start, Date end, String separator, String format) {
        if (start == null || end == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(start) + separator + simpleDateFormat.format(end);
    }

    /**
     * date转字符串格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isEmpty(format)) {
            format = SIMPLE_DATE_FORMAT_STR;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 字符串转日期
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String dateStr) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR).parse(dateStr);
    }

    public static Date parseDate(String dateStr, String format) throws ParseException {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return new SimpleDateFormat(format).parse(dateStr);
    }

    /**
     * 日期加减
     *
     * @param date
     * @param amount
     * @return
     */
    public static Date getDateAmount(Date date, int amount) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, amount);
        return cal.getTime();
    }

    /**
     * 获取日期加减的字符串 (yyyy-mm-dd)
     *
     * @param date
     * @param amount
     * @return
     */
    public static String getDateAmountString(Date date, int amount) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_DAY);
        Date dateAmount = getDateAmount(date, amount);
        return df.format(dateAmount);
    }

    /**
     * 计算两个日期【日期类型】之间的时间距离
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return String 时间距离
     */
    public static String calculateDifference(Date start, Date end) throws ParseException {
        if (start == null||end == null){
            return null;
        }
        Map<String, Long> stringLongMap = calculate(start, end);
        return convertChar(stringLongMap, "day", "天")
                + convertChar(stringLongMap, "hour", "小时")
                + convertChar(stringLongMap, "min", "分钟")
                + convertChar(stringLongMap, "sec", "秒");
    }

    /**
     * 转换字符
     *
     * @param map    数据源
     * @param key    关键字
     * @param suffix 后缀
     * @return String 字符
     */
    public static String convertChar(Map<String, Long> map, String key, String suffix) {
        Long t = map.get(key);
        return null != t && t > 0 ? t + suffix : "";
    }

    /**
     * 计算两个日期【日期类型】之间的时间差值（按分钟计算）
     * @param start 开始时间
     * @param end   结束时间
     */
    public static double getTimeLength(String start,String end) throws ParseException {
        double timeLength = 0.0F;
        if (start == null ||end == null){
            return timeLength;
        }
        long diff = 0;
        Date start1 = DateUtil.parseDate(start, SIMPLE_DATE_FORMAT_STR);
        Date end1 = DateUtil.parseDate(end, SIMPLE_DATE_FORMAT_STR);
        long stime = start1.getTime();
        long etime = end1.getTime();
        if (stime > etime) {
            diff = stime - etime;
        } else {
            diff = etime - stime;
        }
        //时间总长度（毫秒）/分钟
        timeLength = diff / (60 * 1000)*1.0;
        return timeLength;
    }

    /**
     * 计算两个日期【日期类型】之间的时间距离
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return Map<String, Long> 时间距离
     */
    public static Map<String, Long> calculate(Date start, Date end) throws ParseException {
        if (start == null ||end == null){
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DAY);
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff = 0;
//        start = dateFormat.parse(dateFormat.format(start));
//
//        end = dateFormat.parse(dateFormat.format(end));
        long stime = start.getTime();
        long etime = end.getTime();
        if (stime > etime) {
            diff = stime - etime;
        } else {
            diff = etime - stime;
        }
        day = diff / (24 * 5 * 60 * 1000);
        hour = diff / (60 * 60 * 1000) - day * 24;
        min = diff / (60 * 1000) - day * 24 * 60 - hour * 60;
        sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
        Map<String, Long> timeMap = new HashMap<>();
        timeMap.put("day", day);
        timeMap.put("hour", hour);
        timeMap.put("min", min);
        timeMap.put("sec", sec);
        return timeMap;
    }

    /**
     * 计算两个日期相差天数
     *
     * @param startDate 开始时间 endDate 结束时间
     * @return
     */
    public static int countDaysDifference(String startDate, String endDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_DAY);
        Date firstDate = df.parse(startDate);
        Date secondDate = df.parse(endDate);
        int nDay = (int) ((secondDate.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000));
        return nDay;
    }

    public static Date getDateAmount(Date date, int amount, int field) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

    public static Date getLastDay(Date date) {
        return getDateAmount(date, -1);
    }

    public static Date getNextDay(Date date) {
        return getDateAmount(date, 1);
    }

    /**
     * 获取当天0点整点时间
     *
     * @return
     */
    public static Date getCurrDateMidnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 获取当天0点整点时间 LocalDateTime ver.
     *
     * @return
     */
    public static LocalDateTime getCurrLocalDateTimeMidnight() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 日期转换成时间
     *
     * @param dateStr
     * @return
     */
    public static Date transferDate(String dateStr) {
        try {
            if (dateStr == null) {
                return null;
            }
            SimpleDateFormat simpleDateFormat;
            if (dateStr.length() > 10) {
                simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR);
            } else {
                simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_DAY);
            }
            return simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }


    /**
     * 获取指定日期的上一个月的第一天
     *
     * @param dateStr
     * @return
     */
    public static Date getBeforeMonthFirstDay(String dateStr, String format) {
        try {
            Date date = DateUtil.parseDate(dateStr, format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //上个月
            calendar.add(Calendar.MONTH, -1);
            //第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date time = calendar.getTime();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取指定日期上一个月的最后一天
     *
     * @param dateStr
     * @return
     */
    public static Date getBeforeMonthLastDay(String dateStr, String format) {
        try {
            Date date = DateUtil.parseDate(dateStr, format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            calendar.add(Calendar.MONTH, 0);
            //最后一天
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            Date time = calendar.getTime();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取本月第一天
     *
     * @param dateStr
     * @return
     */
    public static Date getThisMonthFirstDay(String dateStr, String format) {
        try {
            Date date = DateUtil.parseDate(dateStr, format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //本月
            calendar.add(Calendar.MONTH, 0);
            //第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date time = calendar.getTime();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取本月最后一天
     *
     * @param dateStr
     * @return
     */
    public static Date getThisMonthLastDay(String dateStr, String format) {
        try {
            Date date = DateUtil.parseDate(dateStr, format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //本月
            calendar.add(Calendar.MONTH, 0);
            //最后一天
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date time = calendar.getTime();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) throws ParseException {
//        2023-03-06T12:16:48.499Z
        String simpleCurrent ="2023-03-06 12:16:48";
        System.out.println("simpleCurrent:"+simpleCurrent);
        String isoCurrent ="2023-03-06T12:16:48.499Z";
        System.out.println("isoCurrent:"+isoCurrent);
        String simple = getIsoToSimpleDateFormat(isoCurrent);
        System.out.println("simple:"+simple);
        String isoFormat = getSimpleToDateIsoFormat(simple);
        System.out.println("isoFormat:"+isoFormat);
        String isoFormatStart= getIsoToSimpleDateFormat("2023-03-06T12:01:00.000Z");
        String isoFormatEnd = getIsoToSimpleDateFormat("2023-03-06T12:16:48.499Z");
        System.out.println("isoFormatStart:"+isoFormatStart);
        System.out.println("isoFormatEnd:"+isoFormatEnd);

    }

    /**
     * 获取开始和结束时间
     *
     * @param time
     * @return
     */
    public static Map<String, String> getStartAndEndTime(Integer time, String startTime, String endTime) {
        Map<String, String> resultMap = new HashMap<>();
        //判断是否为自定义时间类型
        if (!time.equals(CUSTOM_TIME.getCode())) {
            Map<String, String> times = ElasticUtil.getTime(time);
            startTime = times.get(START);
            endTime = times.get(END);
        }
        resultMap.put(START_TIME, startTime.trim());
        resultMap.put(END_TIME, endTime.trim());
        return resultMap;
    }

    /**
     * 将Long类型的时间戳转换成String 类型的时间格式，时间格式为：yyyy-MM-dd HH:mm:ss
     */
    public static String getTimestampToSimpleDateFormat(Long time){
        if (time<=0L){
            return UsualUtil.getString(time);
        }
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT_STR);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()));
    }

}
