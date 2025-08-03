package com.ly.cloud.quartz.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期工具类
 *
 * @author: chenguoqing
 * @mail: chenguoqing@ly-sky.com
 * @date: 2022-10-12
 * @version: 1.0
 */
public class DateUtils {


    /**
     * 获取当前时间String
     */
    public static String getDateSting() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = dateFormat.format(date);
        return dateString;
    }

    /**
     * 获取当前日期是本月的第几周（通过本月有几个周三来判断）
     *
     * @param date 日期
     * @return 本月第n周
     * @throws Exception
     */
    public static int getWeekByWed(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值，变为周一
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
        calendar.add(Calendar.DATE, 2);// 加两天变为周三

        //得到传入日期所在周的周三
        String wed = sdf.format(calendar.getTime());

        // 本月份的天数
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int wek = 3;//判断是周三
        int index = 0;//本月的第几个周三
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= days; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            int weekd = calendar.get(Calendar.DAY_OF_WEEK) - 1;// 注意,Calendar对象默认星期天为1
            if (weekd == wek) {
                String aaa = sdf.format(calendar.getTime());
                list.add(aaa);
                if (wed.equals(aaa)) {
                    index = list.size();
                }
            }
        }
        return index;
    }

    /**
     * 获取当前日期是本月的第几周（通过本月有几个周三来判断）
     *
     * @param dateStr 日期（格式：yyyy-MM-dd）
     * @return 第n周
     * @throws Exception
     */
    public static int getWeekByWed(String dateStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
        Date date = sdf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值，变为周一
        calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - day);
        calendar.add(Calendar.DATE, 2);// 加两天变为周三

        //得到传入日期所在周的周三
        String wed = sdf.format(calendar.getTime());

        // 本月份的天数
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int wek = 3;//判断是周三
        int index = 0;//本月的第几个周三
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= days; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            int weekd = calendar.get(Calendar.DAY_OF_WEEK) - 1;// 注意,Calendar对象默认星期天为1
            if (weekd == wek) {
                String aaa = sdf.format(calendar.getTime());
                list.add(aaa);
                if (wed.equals(aaa)) {
                    index = list.size();
                }
            }
        }
        return index;
    }

    /**
     * 获取今天
     *
     * @return String
     */
    public static String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 获取昨天
     *
     * @return String
     */
    public static String getYestoday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    /**
     * 获取本月开始日期
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00";
    }

    /**
     * 获取本月最后一天
     *
     * @return String
     **/
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00";
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static String getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    /**
     * 获取本年的第一天
     *
     * @return String
     **/
    public static String getYearStart() {
        return new SimpleDateFormat("yyyy").format(new Date()) + "-01-01 00:00:00";
    }

    /**
     * 获取本年的最后一天
     *
     * @return String
     **/
    public static String getYearEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date currYearLast = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(currYearLast) + " 23:59:59";
    }

    /**
     * String 转换成date
     *
     * @param stringDate 字符串时间
     * @return Date
     */
    public static Date setStringToDate(String stringDate) throws ParseException {
        DateFormat dateFormat = null;
        if (stringDate.length() == 10) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return dateFormat.parse(stringDate);
    }

    /**
     * 获取当前日期年份
     *
     * @param date 日期
     * @return 年份
     * @throws Exception
     */
    public static String getYear(Date date) throws Exception {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        return String.valueOf(now.get(Calendar.YEAR));
    }

    /**
     * 获取当前日期月份
     *
     * @param date 日期
     * @return 年份
     * @throws Exception
     */
    public static String getMonth(Date date) throws Exception {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        return String.valueOf(now.get(Calendar.MONTH) + 1);
    }


    public static void main(String[] args) throws Exception {

        System.out.println(getMonth(setStringToDate(getToday())));

//        System.out.println("获取今天:"+getToday());
//        System.out.println("获取今天:"+setStringToDate(getToday()));
//        System.out.println("获取昨天:"+getYestoday());
//        System.out.println("获取昨天:"+setStringToDate(getYestoday()));
//        System.out.println("获取本月开始日期:"+getMonthStart());
//        System.out.println("获取本月开始日期:"+setStringToDate(getMonthStart()));
//        System.out.println("获取本月最后一天:"+getMonthEnd());
//        System.out.println("获取本月最后一天:"+setStringToDate(getMonthEnd()));
//        System.out.println("获取本周的第一天:"+getWeekStart());
//        System.out.println("获取本周的第一天:"+setStringToDate(getWeekStart()));
//        System.out.println("获取本周的最后一天:"+getWeekEnd());
//        System.out.println("获取本周的最后一天:"+setStringToDate(getWeekEnd()));
//        System.out.println("获取本年的第一天:"+getYearStart());
//        System.out.println("获取本年的第一天:"+setStringToDate(getYearStart()));
//        System.out.println("获取本年的最后一天:"+getYearEnd());
//        System.out.println("获取本年的最后一天:"+setStringToDate(getYearEnd()));
    }
}
