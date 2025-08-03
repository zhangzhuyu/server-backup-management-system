package com.ly.cloud.backup.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 环比用时间
 * */
public class SqlTimeUtil {
    public static final Logger logger= LoggerFactory.getLogger(SqlTimeUtil.class);
    public static final String DateFormat="yyyy-MM-dd HH:mm:ss";
    //上周一
    public static String getLastWeekStart() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.WEEK_OF_YEAR,-1);
        c.set(Calendar.DAY_OF_WEEK,1);
        String weekStart = format.format(c.getTime())+" 00:00:00";
        return weekStart;
    }
    //上周日
    public static String getLastWeekEnd() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.WEEK_OF_YEAR,-1);
        c.set(Calendar.DAY_OF_WEEK,7);
        String weekEnd = format.format(c.getTime())+" 23:59:59";
        return weekEnd;
    }

    public static String getThisWeekStart() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c=Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK,1);
        String weekStart = format.format(c.getTime())+" 00:00:00";
        return weekStart;
    }
    //上月第一天
    public static String getLastMonthStart() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String monthStart = format.format(c.getTime())+" 00:00:00";
        return monthStart;
    }
    //上月最后一天
    public static String getLastMonthEnd() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MONTH,-1);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String monthEnd = format.format(ca.getTime())+" 23:59:59";
        return monthEnd;
    }

    //本月第一天
    public static String getThisMonthStart() {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String monthStart = format.format(c.getTime())+" 00:00:00";
        return monthStart;
    }

    public static String getNow() {
        SimpleDateFormat format=new SimpleDateFormat(DateFormat);
        Date d=new Date();
        return format.format(d);
    }

    public static void main(String[] args){
//        System.out.println(getLastWeekStart());
//        System.out.println(getLastWeekEnd());
//        System.out.println(getThisWeekStart());
//        System.out.println(getLastMonthStart());
//        System.out.println(getLastMonthEnd());
//        System.out.println(getThisMonthStart());
        System.out.println(getTimeDiff("2023-02-24 08:00:00","2023-02-23 07:00:00"));
        Long diff=0l;
        SimpleDateFormat format=new SimpleDateFormat(DateFormat);
        try{
            Date d0=format.parse("2023-02-24 08:00:00");
            Date d1=format.parse("2023-02-23 08:00:00");
            diff =d0.getTime()-d1.getTime();
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(subtract("2023-02-24T08:12:00.123Z",diff));
    }

    //getTimeDiff 获取两个时间的时间差ms数
    public static Long getTimeDiff(String loadStartCurrent, String loadStartPast) {
        SimpleDateFormat format=new SimpleDateFormat(DateFormat);
        try{
            Date d0=format.parse(loadStartCurrent);
            Date d1=format.parse(loadStartPast);
            return d0.getTime()-d1.getTime();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
       return 0L;
    }

    public static String subtract(String time, Long dayDiff) {
        String formatStr="yyyy-MM-dd HH:mm:ss.SSS";
        SimpleDateFormat format=new SimpleDateFormat(formatStr);
        time=time.replace("T"," ").replace("Z","");
        try{
            Date d=format.parse(time);
            Long l=d.getTime()-dayDiff;
            DateTimeFormatter fmt=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String s=fmt.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault()));
            return s.replace(" ","T")+"+08:00";
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

}
