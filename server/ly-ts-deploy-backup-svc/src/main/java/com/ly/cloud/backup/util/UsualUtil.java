package com.ly.cloud.backup.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ly.cloud.backup.common.constant.CommonConstant;
import com.ly.cloud.backup.common.enums.CommonEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ly.cloud.backup.common.constant.CommonConstant.NULL;
import static com.ly.cloud.backup.util.ElasticUtil.getNoRound;

/**
 * Class Name: UsualUtil Description: 自定义工具类
 *
 * @author: jiangzhongxin
 * @mail: jiangzhongxin@ly-sky.com
 * @date: 2022年01月22日 9:44
 * @copyright: 2018 LIANYI TECHNOLOGY CO.,LTD. All Rights Reserved. 联奕科技有限公司
 * @version: 1.0
 */
@Slf4j
public class UsualUtil {

    public static final String GET = "get";
    public static final String SET = "set";
    public static final String NAME_KEY = "name";
    public static final String CODE_KEY = "code";
    public static final String VALUE_KEY = "value";
    public static final String ALL_DATA = "all_data";
    public static final Class<?> COMMON_ENUMS_CLASS = CommonEnums.class;
    /**
     * 集合默认初始容量
     */
    private static final int INITIAL_CAPACITY = 10;

    /**
     * 判断集合为空
     *
     * @param coll（集合）
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean collIsEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 判断集合全为空
     *
     * @param coll（集合）
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean collIsEmpty(Collection<?>... coll) {
        if (objIsNull(coll)) {
            return false;
        }
        boolean flag = true;
        for (Collection<?> collection : coll) {
            if (collIsNotEmpty(collection)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断集合不为空
     *
     * @param coll（集合）
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean collIsNotEmpty(Collection<?> coll) {
        return !collIsEmpty(coll);
    }

    /**
     * 判断集合全不为空
     *
     * @param coll（集合）
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean collIsNotEmpty(Collection<?>... coll) {
        if (objIsNull(coll)) {
            return false;
        }
        boolean flag = true;
        for (Collection<?> collection : coll) {
            if (collIsEmpty(collection)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断String为空
     *
     * @param str : 字符
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean strIsEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean strIsEmpty(String... str) {
        return !strIsNotEmpty(str);
    }

    /**
     * 判断String不为空
     *
     * @param str : 字符
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean strIsNotEmpty(String str) {
        return !strIsEmpty(str);
    }

    /**
     * 判断多个String不为空
     *
     * @param temps : 多个字符
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean strIsNotEmpty(String... temps) {
        if (objIsNull(temps)) {
            return false;
        }
        int count = 0;
        for (String str : temps) {
            if (strIsEmpty(str) || strIsEmpty(str.trim())) {
                return false;
            }
            count++;
        }
        return count == temps.length;
    }

    /**
     * 判断Map为空
     *
     * @param map : map集合
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean mapIsEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断Map不为空
     *
     * @param map : map集合
     * @return boolean
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static boolean mapIsNotEmpty(Map<?, ?> map) {
        return !mapIsEmpty(map);
    }

    /**
     * list集合根据n分组
     *
     * @param list : 集合
     * @return List
     * @date: 2021/9/17 14:05
     */
    public static <T> List<List<T>> fixedGrouping(List<T> list, int n) {
        if (collIsNotEmpty(list) && n > 0) {
            List<List<T>> result = new ArrayList<>();
            int remainder = list.size() % n;
            int size = list.size() / n;
            for (int i = 0; i < size; ++i) {
                List<T> subset;
                subset = list.subList(i * n, (i + 1) * n);
                result.add(subset);
            }
            if (remainder > 0) {
                List<T> subset;
                subset = list.subList(size * n, size * n + remainder);
                result.add(subset);
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 根据key抽取list集合某个字段
     *
     * @param list : 集合
     * @return List
     * @author: jiangzhongxin
     * @date: 2021/9/17 14:05
     */
    public static <T> List<Object> extractList(List<T> list, Class<T> dataClass, String key) {
        try {
            if (collIsEmpty(list) || objIsNull(dataClass) || strIsEmpty(key)) {
                return null;
            }
            List<Object> result = new ArrayList<>(list.size());
            Method[] methods = dataClass.getDeclaredMethods();
            int length = methods.length;
            int i;
            for (T t : list) {
                for (i = 0; i < length; ++i) {
                    Method method = methods[i];
                    String name = method.getName();
                    if (name.startsWith(GET) && name.contains(key)) {
                        Object value = method.invoke(t);
                        if (objNotNull(value)) {
                            result.add(value);
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 通过【COMMON_ENUMS_CLASS】枚举类给数据集合【dataList】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param dataList           : 数据对象集合
     * @param prefixAndFieldsMap : （枚举类关键字的前缀，data成员属性名）集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(List<T> dataList, Map<String, List<String>> prefixAndFieldsMap) {
        if (collIsNotEmpty(dataList) && mapIsNotEmpty(prefixAndFieldsMap)) {
            for (T t : dataList) {
                assignmentByEnums(t, prefixAndFieldsMap);
            }
        }
    }

    /**
     * 通过【COMMON_ENUMS_CLASS】枚举类给数据【data】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param data               : 数据对象
     * @param prefixAndFieldsMap : （枚举类关键字的前缀，data成员属性名）集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(T data, Map<String, List<String>> prefixAndFieldsMap) {
        if (objNotNull(data) && mapIsNotEmpty(prefixAndFieldsMap)) {
            Set<Map.Entry<String, List<String>>> entries = prefixAndFieldsMap.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                if (objNotNull(entry)) {
                    assignmentByEnums(data, entry.getValue(), COMMON_ENUMS_CLASS, entry.getKey());
                }
            }
        }
    }

    /**
     * 通过【COMMON_ENUMS_CLASS】枚举类给数据【data】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param data   : 数据对象
     * @param fields : data成员属性名
     * @param prefix : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(T data, List<String> fields, String prefix) {
        assignmentByEnums(data, fields, COMMON_ENUMS_CLASS, prefix);
    }

    /**
     * 通过【enumClass】枚举类给数据集合【dataList】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param dataList : 数据对象集合
     * @param fields   : data成员属性名
     * @param prefix   : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(List<T> dataList, List<String> fields, String prefix) {
        if (collIsNotEmpty(dataList, fields) && strIsNotEmpty(prefix)) {
            for (T data : dataList) {
                assignmentByEnums(data, fields, COMMON_ENUMS_CLASS, prefix);
            }
        }
    }

    /**
     * 通过【enumClass】枚举类给数据【dataList】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param data      : 数据对象
     * @param fields    : data成员属性名
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(T data, List<String> fields, Class<?> enumClass) {
        assignmentByEnums(data, fields, enumClass, ALL_DATA);
    }

    /**
     * 通过【enumClass】枚举类给数据集合【dataList】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param dataList  : 数据对象集合
     * @param fields    : data成员属性名
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @param prefix    : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(List<T> dataList, List<String> fields, Class<?> enumClass, String prefix) {
        if (collIsNotEmpty(dataList, fields) && objNotNull(enumClass, prefix)) {
            for (T data : dataList) {
                assignmentByEnums(data, fields, enumClass, prefix, CODE_KEY, VALUE_KEY);
            }
        }
    }

    /**
     * 通过【enumClass】枚举类给数据【data】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param data      : 数据对象
     * @param fields    : data成员属性名
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @param prefix    : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(T data, List<String> fields, Class<?> enumClass, String prefix) {
        assignmentByEnums(data, fields, enumClass, prefix, CODE_KEY, VALUE_KEY);
    }

    /**
     * 通过【enumClass】枚举类给数据【data】的【fields】成员属性们重新赋值 code -> value 这种形式
     *
     * @param data      : 数据对象
     * @param fields    : data成员属性名
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @param prefix    : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @param codeKey   : 枚举类的成员属性名，基本默认为 code
     * @param valueKey  : 枚举类的成员属性名，基本默认为 value
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static <T> void assignmentByEnums(T data, List<String> fields, Class<?> enumClass, String prefix, String codeKey, String valueKey) {
        try {
            if (objContainNull(data, enumClass, fields)) {
                return;
            }
            Class<?> dataClass = data.getClass();
            List<Map<String, Object>> enumValues = getInvokeValue(enumClass, prefix, codeKey, valueKey);
            if (objNotNull(dataClass) && collIsNotEmpty(enumValues)) {
                for (String field : fields) {
                    Method getMethod = dataClass.getDeclaredMethod(GET + upperFirstCapse(field));
                    if (objNotNull(getMethod)) {
                        // 通过getter方法成员属性值
                        Object getValue = getMethod.invoke(data);
                        Method setMethod = dataClass.getDeclaredMethod(SET + upperFirstCapse(field), getValue.getClass());
                        if (objNotNull(setMethod)) {
                            enumValues.stream()
                                    .filter(r -> objEquals(r.get(codeKey), getValue))
                                    .forEach(item -> {
                                        try {
                                            Object o = item.get(valueKey);
                                            if (objNotNull(o)) {
                                                // 通过setter方法给成员属性赋值
                                                setMethod.invoke(data, o);
                                            }
                                        } catch (Exception e) {
                                            log.error(e.getMessage(), e);
                                        }
                                    });
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 通过一定参数获取枚举类相关name、code、value数据集合
     *
     * @return List<Map < String, Object>> ：枚举类相关name、code、value数据集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static List<Map<String, Object>> getInvokeValue() {
        return getInvokeValue(COMMON_ENUMS_CLASS);
    }

    /**
     * 通过一定参数获取枚举类相关name、code、value数据集合
     *
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @return List<Map < String, Object>> ：枚举类相关name、code、value数据集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static List<Map<String, Object>> getInvokeValue(Class<?> enumClass) {
        return getInvokeValue(enumClass, ALL_DATA);
    }

    /**
     * 通过一定参数获取枚举类相关name、code、value数据集合
     *
     * @param prefix : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @return List<Map < String, Object>> ：枚举类相关name、code、value数据集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static List<Map<String, Object>> getInvokeValue(String prefix) {
        return getInvokeValue(COMMON_ENUMS_CLASS, prefix, CODE_KEY, VALUE_KEY);
    }

    /**
     * 通过一定参数获取枚举类相关name、code、value数据集合
     *
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @param prefix    : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @return List<Map < String, Object>> ：枚举类相关name、code、value数据集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static List<Map<String, Object>> getInvokeValue(Class<?> enumClass, String prefix) {
        return getInvokeValue(enumClass, prefix, CODE_KEY, VALUE_KEY);
    }

    /**
     * 通过一定参数获取枚举类相关name、code、value数据集合
     *
     * @param enumClass : 对应的枚举类，基本默认可用公共枚举类 COMMON_ENUMS_CLASS
     * @param prefix    : 枚举类关键字的前缀，由于code并不唯一，故用于此筛选枚举类数据，可用默认ALL_DATA不进行筛选数据
     * @param codeKey   : 枚举类的成员属性名，基本默认为 code
     * @param valueKey  : 枚举类的成员属性名，基本默认为 value
     * @return List<Map < String, Object>> ：枚举类相关name、code、value数据集合
     * @author: jiangzhongxin
     * @date: 2022/3/18 14:36
     */
    public static List<Map<String, Object>> getInvokeValue(Class<?> enumClass, String prefix, String codeKey, String valueKey) {
        try {
            if (enumClass.isEnum()) {
                // 获取该枚举类对应的get方法
                Method getCode = enumClass.getDeclaredMethod(GET + upperFirstCapse(codeKey));
                Method getValue = enumClass.getDeclaredMethod(GET + upperFirstCapse(valueKey));
                Object[] enumConstants = enumClass.getEnumConstants();
                if (objNotNull(getCode, getValue, enumConstants)) {
                    List<Map<String, Object>> list = new ArrayList<>(enumConstants.length);
                    Map<String, Object> map;
                    for (Object obj : enumConstants) {
                        map = new HashMap<>(3);
                        String name = objCastString(obj);
                        // 判断枚举类的成员【name】前缀是否符合，若prefix为全部数据【ALL_DATA】关键字则直接放行
                        if (objEquals(prefix, ALL_DATA) || strStartsWith(name, prefix)) {
                            map.put(NAME_KEY, name);
                            map.put(codeKey, objCastString(getCode.invoke(obj)));
                            map.put(valueKey, objCastString(getValue.invoke(obj)));
                            list.add(map);
                        }
                    }
                    return list;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 首字母大写
     *
     * @author: jiangzhongxin
     * @date: 2022/3/18 10:45
     */
    public static String upperFirstCapse(String name) {
        if (strIsEmpty(name)) {
            return null;
        }
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 首字母小写
     *
     * @author: jiangzhongxin
     * @date: 2022/3/18 10:45
     */
    public static String lowerFirstCapse(String name) {
        if (strIsEmpty(name)) {
            return null;
        }
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 对象为空值
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objIsNull(Object obj) {
        return !objNotNull(obj);
    }

    /**
     * 对象不为空值
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objNotNull(Object obj) {
        return Optional.ofNullable(obj).isPresent();
    }

    /**
     * 对象中不存在任何空值
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objNotNull(Object... obj) {
        if (objIsNull(obj)) {
            return false;
        }
        boolean flag = true;
        for (Object o : obj) {
            if (objIsNull(o)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 对象中存在空值时返回true
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objContainNull(Object... obj) {
        if (objIsNull(obj)) {
            return true;
        }
        boolean flag = false;
        for (Object o : obj) {
            if (objIsNull(o)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断字符前缀是否匹配
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean strStartsWith(String nno, String prefix) {
        if (objIsNull(nno)) {
            return false;
        }
        return nno.startsWith(prefix);
    }

    /**
     * 如果参数彼此相等则返回 {@code true}，否则返回 {@code false}。因此，如果两个参数都是 {@code null}，则返回 {@code true}，如果恰好一个参数是 {@code null}，则返回 {@code false}。否则，相等性通过使用第一个参数的 {@link Object#equals equals} 方法来确定。
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objEquals(Object nno, Object o) {
        return Objects.equals(nno, o);
    }

    /**
     * 比较两对象不等
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static Boolean objUnequals(Object nno, Object o) {
        return !objEquals(nno, o);
    }

    /**
     * 对象转String
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static String objCastString(Object obj) {
        return Optional.ofNullable(obj).map(String::valueOf).orElse(null);
    }

    /**
     * 对象转Integer
     *
     * @author: jiangzhongxin
     * @date: 2022/3/18 10:42
     */
    public static Integer objCastInteger(Object object) {
        if(objIsNull(object)){
            return 0;
        }
        String temp = UsualUtil.objCastString(object);
        if(strIsEmpty(temp)){
            return 0;
        }
        return Integer.valueOf(temp);
    }

    /**
     * 对象转long
     *
     * @param object : 对象
     * @return long 数据
     * @author jiangzhongxin
     * @date 2022/3/25 9:20
     */
    public static long objCastLong(Object object) {
        if(objIsNull(object)){
            return 0L;
        }
        String temp = UsualUtil.objCastString(object);
        if(strIsEmpty(temp)){
            return 0L;
        }
        return Long.parseLong(temp);
    }

    /**
     * 转List<T>
     *
     * @author: jiangzhongxin
     * @date: 2022/3/16 16:42
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        if (objIsNull(obj)) {
            return null;
        }
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 目标对象转换map集合数据
     *
     * @param target : 目标对象
     * @param kCalzz : 类对象
     * @param vCalzz : 类对象
     * @return List<Map<K, V>> : 转换map集合数据
     * @author jiangzhongxin
     * @date 2022/3/25 9:20
     */
    public static <K, V> List<Map<K, V>> castListMap(Object target, Class<K> kCalzz, Class<V> vCalzz) {
        List<Map<K, V>> result = new ArrayList<>();
        if (target instanceof List<?>) {
            for (Object mapObj : (List<?>) target) {
                if (mapObj instanceof Map<?, ?>) {
                    Map<K, V> map = new HashMap<>(16);
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) mapObj).entrySet()) {
                        map.put(kCalzz.cast(entry.getKey()), vCalzz.cast(entry.getValue()));
                    }
                    result.add(map);
                }
            }
            return result;
        }
        return null;
    }

    /**
     * 目标字符串【target】不能带前缀【prefixes】
     *
     * @param target : 目标字符串
     * @param prefixes : 前缀
     * @return boolean : 结果
     * @author jiangzhongxin
     * @date 2022/3/25 9:20
     */
    public static boolean notAllStartsWith(String target, String... prefixes) {
        if(strIsEmpty(target) || objIsNull(prefixes) || UsualUtil.collIsEmpty(Arrays.asList(prefixes))){
            return false;
        }
        boolean flag = true;
        for (String prefix : prefixes) {
            if(target.startsWith(prefix)){
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 比较【dateStr】是否在【startTimeStr】、【endIndex】之间
     *
     * @param dateStr : 目标时间字符
     * @param startTimeStr : 开始时间字符
     * @param endTimeStr : 结束时间字符
     * @param endIndex : substring 结束索引
     * @return Boolean
     * @author jiangzhongxin
     * @date 2022/3/28 17:41
     */
    public static boolean compareDate(String dateStr, String startTimeStr, String endTimeStr, Integer endIndex){
        try {
            if(strIsNotEmpty(dateStr, startTimeStr, endTimeStr)){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(objIsNull(endIndex) ? CommonConstant.SIMPLE_DATE_FORMAT_STR : CommonConstant.SIMPLE_DATE_FORMAT_STR.substring(0, endIndex));
                Date date = new SimpleDateFormat(CommonConstant.SIMPLE_DATE_FORMAT_STR).parse(dateStr);
                Date startTime = simpleDateFormat.parse(startTimeStr);
                Date endTime = simpleDateFormat.parse(endTimeStr);
                if (startTime.before(endTime) && date.after(startTime) && date.before(endTime)) {
                    return true;
                }
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 对象转map
     *
     * @param bean : 对象
     * @return Map<String, Object>
     * @author jiangzhongxin
     * @date 2022/3/29 10:29
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (objNotNull(bean)) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 对象转map
     *
     * @param bean : 对象
     * @return Map<String, Object>
     * @author jiangzhongxin
     * @date 2022/3/29 10:30
     */
    public static <T> Map<String, Object> beanToMap2(Object bean) throws Exception {
        Map<String, Object> map = new HashMap<>(INITIAL_CAPACITY);
        if(objNotNull(bean)){
            // 获取类的属性描述器
            BeanInfo beaninfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            // 获取类的属性集
            PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
            for (PropertyDescriptor property : pro) {
                String key = property.getName();
                Method get = property.getReadMethod();
                if(objNotNull(key, get)){
                    Object value = get.invoke(bean);
                    map.put(key, objIsNull(null) ? "" : value);
                }
            }
        }
        return map;
    }

    /**
     * map集合转对象
     *
     * @param map : 集合
     * @param bean : 对象
     * @author jiangzhongxin
     * @date 2022/3/29 10:30
     */
    public static <T> void mapToBean(Map<String, Object> map, T bean) {
        if(objNotNull(map, bean)){
            BeanMap beanMap = BeanMap.create(bean);
            beanMap.putAll(map);
        }
    }

    /**
     * 对象集合转List<Map<String, Object>>
     *
     * @param objList : 对象集合
     * @return List<Map<String, Object>>
     * @author jiangzhongxin
     * @date 2022/3/29 10:30
     */
    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (collIsNotEmpty(objList)) {
            Map<String, Object> map;
            T bean;
            for (T t : objList) {
                bean = t;
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * List<Map<String, Object>>转【clazz】集合
     *
     * @param mapList : map集合
     * @param clazz : 类
     * @return List<T>
     * @author jiangzhongxin
     * @date 2022/3/29 10:30
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> mapList, Class<T> clazz) {
        List<T> list = Lists.newArrayList();
        try {
            if (collIsNotEmpty(mapList) && objNotNull(clazz)) {
                Map<String, Object> map;
                T bean;
                for (Map<String, Object> stringObjectMap : mapList) {
                    map = stringObjectMap;
                    bean = clazz.newInstance();
                    mapToBean(map, bean);
                    list.add(bean);
                }
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 获取string字符串，如果为空则返回""
     * @param object
     * @return
     */
    public static String getString(Object object){
        try {
            if (Objects.isNull(object)|| NULL.equals(object)){
                return "";
            }else {
                return object.toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return "";
    }

    /**
     * 获取string字符串，如果为空则返回""
     * @param object
     * @return
     */
    public static float getFloat(Object object){
        try {
            if (Objects.isNull(object)|| NULL.equals(object)){
                return 0;
            }else {
                return Float.parseFloat(object.toString());
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }


    /**
     * 获取string字符串，如果为空则返回""
     * @param object
     * @return
     */
    public static int getInt(Object object){
        try {
            if (Objects.isNull(object)|| NULL.equals(object)){
                return 0;
            }else {
                return Integer.parseInt(getNoRound(Double.parseDouble(object.toString())));
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }

    /**
     * 获取string字符串，如果为空则返回0
     * @param object
     * @return
     */
    public static long getLong(Object object){
        try {
            if (Objects.isNull(object)|| NULL.equals(object)){
                return 0;
            }else {
                double v = Double.parseDouble(object.toString());
                String noRound = getNoRound(v);
                if (Double.parseDouble(noRound)<1L){
                    return 1L;
                }
                return Long.parseLong(getNoRound(Double.parseDouble(object.toString())));
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }

    /**
     * 获取string字符串，如果为空则返回0
     * @param object
     * @return
     */
    public static double getDouble(Object object){
        try {
            if (Objects.isNull(object)|| NULL.equals(object)){
                return 0;
            }else {
                return Double.parseDouble(object.toString());
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }


    /**
     * 获取string字符串，判断是否为空，包含不等于字符串'null'。
     * @param object
     * @return
     */
    public static boolean isNotEmpty(Object object){
        if (!Objects.isNull(object) && !NULL.equalsIgnoreCase(object.toString())){
            return true;
        }
        return false;
    }

    /**
     * 获取string字符串，判断是否为空，包含不等于字符串'null'。
     * @param object
     * @return
     */
    public static boolean isNotEmpty(String object){
        if (StringUtils.isNotEmpty(object) && !NULL.equalsIgnoreCase(object)){
            return true;
        }
        return false;
    }

}
