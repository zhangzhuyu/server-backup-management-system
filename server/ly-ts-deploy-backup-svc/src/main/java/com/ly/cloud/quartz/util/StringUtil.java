package com.ly.cloud.quartz.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lzx
 * Description
 * @2019年12月3日
 * @version 1.0
 */
public class StringUtil {
	
	/**
	 * 字符串是否不为空
	 * @param string
	 * @return
	 */
	public static boolean isNotEmpty(String string){
		boolean result = false;
		if(string != null && StringUtils.isNotEmpty(string) && !string.equals("null")){
			result = true;
		}
		return result;
	}
	/**
	 * 字符串是否为空
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string){
		boolean result = false;
		if(string == null || StringUtils.isEmpty(string)){
			result = true;
		}
		return result;
	}
	/**
	 * 获取字符型请求参数
	 * @param requestParamName
	 * @param requestParams
	 * @return
	 */
	public static String getStringRequestParam(String requestParamName, Map<String, Object> requestParams) {
		String result = requestParams.get(requestParamName) == null ? null
				: (String) requestParams.get(requestParamName);
		return result;
	}
	/**
	 * 获取字符型请求参数
	 * @param requestParamName
	 * @param requestParams
	 * @return
	 */
	public static int getIntRequestParam(String requestParamName, Map<String, Object> requestParams) {
		int result = requestParams.get(requestParamName) == null ? -1 : (int) requestParams.get(requestParamName);
		return result;
	}

	/**
	 * null或空白字符串替换为指定字符串
	 * @param str
	 * @param replace
	 * @return
	 */
	public static String emptyToCharacter(String str, String replace) {
		if (StringUtils.isEmpty(str)) {
			return replace;
		}
		return str;
	}

	/**
	 * 替换标点符号
	 * @param str
	 * @param
	 * @return
	 */
	public static String replacePunctuation(String str) {
		str = str.replaceAll("[\\pP\\p{Punct}]", "");
		return str;
	}

	/**
	 * 逗号分隔的字符串去重
	 *
	 * @param str 用逗号分隔的字符串，如（人事管理系统,全局库,全局库,全局库,全局库,前置库）
	 * @return 去重后的字符串，去重后变成（人事管理系统,全局库,前置库）
	 */
	public static String commaStringDistinct(String str) {
		if (isEmpty(str)) {
			return "";
		} else {
			return Arrays.stream(str.split(",")).distinct().map(String::trim).collect(Collectors.joining(","));
		}
	}

	public static StringBuilder hideStringInformation(StringBuilder command,String password){
		int start = command.indexOf(password); // 获取子串 "World" 的开始位置
		int end = start + password.length(); // 获取子串 "World" 的结束位置加一
		if (start != -1) {
			command.replace(start, end, "*****"); // 使用 replace 方法进行替换
		}
		return command;
	}
}
