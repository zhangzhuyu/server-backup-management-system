package com.ly.cloud.backup.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SYC
 * @Date: 2022/5/24 15:24
 * @Description 通过反射方式对数组进行按字段模糊匹配
 */
public class GetTargetList {


    public static<T> List<T> getList(List<T> list, String keyword) {
        if (StringUtils.isEmpty(keyword)|| CollectionUtils.isEmpty(list)){
            return list;
        }
        ArrayList<T> results = new ArrayList<>();
        list.forEach(obj -> {
            StringBuilder sb = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            Field[] fields = obj.getClass().getDeclaredFields();

            //设置大小写不敏感  加Pattern.CASE_INSENSITIVE
            Pattern pattern = Pattern.compile(keyword,Pattern.CASE_INSENSITIVE);
            for (int i = 0, len = fields.length; i < len; i++) {
                try {
                    // 获取原来的访问控制权限
                    boolean accessFlag = fields[i].isAccessible();
                    // 修改访问控制权限
                    fields[i].setAccessible(true);
                    // 获取在对象f中属性fields[i]对应的对象中的变量
                    Object o;
                    try {
                        o = fields[i].get(obj);
                        //字段是null跳过
                        if (o==null) {
                            continue;
                        }
                        String s = o.toString();
                        //根据需要排除部分属性值
                       /* if ("truefalse".contains(s)) {
                            continue;
                        }*/
                        //拼接对象中的值
                        sb.append(s);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    // 恢复访问控制权限
                    fields[i].setAccessible(accessFlag);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
            Matcher matcher = pattern.matcher(sb);
            //模糊匹配                可根据需求调整匹配规则
            if (matcher.find()) {
                results.add(obj);
                System.out.println("检索条件keyword:"+keyword);
                System.out.println("满足检索条件的返回值obj:"+obj);
            }

        });
        return results;
    }
}
