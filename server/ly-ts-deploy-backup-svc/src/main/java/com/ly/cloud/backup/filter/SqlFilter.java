package com.ly.cloud.backup.filter;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @author SYC
 * @Date: 2022/10/13 16:16
 * @Description 用于过滤sql关键字，处理sql盲注的问题
 */
@WebFilter(urlPatterns = "/*", filterName = "sqlFilter")
@Configuration
public class SqlFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest request = servletRequest;
        ServletResponse response = servletResponse;
        //获得所有请求参数名
        Enumeration<String> names = request.getParameterNames();
        String sql = "";
        while (names.hasMoreElements()) {
            //得到参数名
            String name = names.nextElement().toString();
            //得到参数对应值
            String[] values = request.getParameterValues(name);
            for (int i = 0; i < values.length; i++) {
                sql += values[i];
            }
        }

        filterChain.doFilter(request, response);

//        if (sqlValidate(sql)) {
//            throw new IOException("您发送请求中的参数中含有非法字符!");
//        } else {
//            filterChain.doFilter(request, response);
//        }

    }

    // 效验
    protected static boolean sqlValidate(String str) {
        String s = str.toLowerCase();// 统一转为小写
        String badStr = "select|update|and|delete|insert|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|table|"
//        String badStr = "select|update|and|or|delete|insert|truncate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute|table|"
                + "char|declare|sitename|xp_cmdshell|like|from|grant|use|group_concat|column_name|"
                + "information_schema.columns|table_schema|union|where|order|by|"
                + "'\\*|\\;|\\-|\\--|\\+|\\,|\\//|\\/|\\%|\\#";
        // 过滤掉的sql关键字，特殊字符前面需要加\\进行转义
        String[] split = badStr.toLowerCase().split("\\|");
        long count = Arrays.stream(split).filter(f -> s.contains(f)).count();
       if (count>0){
           return true;
       }
       return false;
    }

}
