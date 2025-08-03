package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class Name DateToString 
 *
 * Description 时间转换工具类
 * @author zhanglei
 * @mail zhanglei@ly-sky.com 
 * @date  
 * @version 1.0
 */
public class DateToString {

	/**
	 * Method Name: getFormatDate
	 *
	 * Description: 当前时间转换成yyyy-MM-dd HH:mm:ss格式字符串
	 * @return String
	 * @exception 	
	 * @author  
	 * @mail  
	 * @date  
	 */
	public static String getFormatDate(){
		Date date = new Date();
		return getFormatDate(date);
	}


	public static String getFormatDate(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = format.format(date);
		return dateString;
	}

	public static String getFormatDateConsistentNums(){
		Date date = new Date();
		return getFormatDateConsistentNums(date);
	}

	public static String getFormatDateConsistentNums(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		String dateString = format.format(date);
		return dateString;
	}

	
	/**
	 * 
	* @Title: getFormatDate 
	* @Description: 日期计算并格式化  
	* @param @param field  单位   Calendar.YEAR Calendar.MONTH Calendar.DAY_OF_MONTH Calendar.HOUR
	* @param @param amount
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	public static String getFormatDate(int field, int amount){
		//获取当前时间
		Date date = new Date();
		Calendar ca=Calendar.getInstance();		
		ca.setTime(date);
		ca.add(field, amount);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = format.format(ca.getTime());
		return dateString;
	}
	
	public static void main(String[] args) {
		System.out.println(DateToString.getFormatDateConsistentNums(new Date()));
	}
}
