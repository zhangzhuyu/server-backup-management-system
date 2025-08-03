package com.ly.cloud.backup.common.aspect.dataSourceSwitch;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class Name FastJsonUtils 
 *
 * Description Json工具类，实现JSON与Java Bean的互相转换 
 * @author 
 * @mail 
 * @date 
 * @version 1.0
 */
public class FastJsonUtils {  
  
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static JsonFactory jsonFactory = new JsonFactory();
  

  
    /** 
     * 泛型返回，json字符串转对象 
     * 2015年4月3日上午10:42:19 
     * auther:shijing 
     * @param jsonAsString 
     * @param pojoClass 
     * @return 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     * @throws IOException 
     */  
    public static <T> T fromJson(String jsonAsString, Class<T> pojoClass) throws JsonParseException, JsonMappingException, IOException {
//    	objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//    	objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    	return objectMapper.readValue(jsonAsString, pojoClass);
    }  
  
    public static <T> T fromJson(FileReader fr, Class<T> pojoClass) throws JsonParseException, JsonMappingException, IOException  {  
    	return objectMapper.readValue(fr, pojoClass);
    }  
  
    /** 
     * Object对象转json 
     * 2015年4月3日上午10:41:53 
     * auther:shijing 
     * @param pojo 
     * @return 
     * @throws JsonMappingException
     * @throws IOException 
     */  
    public static String toJson(Object pojo)  {  
        try {
			return toJson(pojo, false);
		} catch (Exception e) {
			e.printStackTrace();
		}  
        return null;
    }  
  
    public static String toJson(Object pojo, boolean prettyPrint)  {  
        StringWriter sw;
		try {
			sw = new StringWriter();  
			JsonGenerator jg = jsonFactory.createJsonGenerator(sw);
			if (prettyPrint) {  
			    jg.useDefaultPrettyPrinter();  
			}  
			objectMapper.writeValue(jg, pojo);
			
			return sw.toString();  
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return null;
    }  
  
    public static void toJson(Object pojo, FileWriter fw, boolean prettyPrint)  {  
        try {
			JsonGenerator jg = jsonFactory.createJsonGenerator(fw);  
			if (prettyPrint) {  
			    jg.useDefaultPrettyPrinter();  
			}  
			objectMapper.writeValue(jg, pojo);
		}  catch (Exception e) {
			e.printStackTrace();
		}  
    }  
  
    /** 
     * json字符串转Map 
     * 2015年4月3日上午10:41:25 
     * auther:shijing 
     * @param jsonStr 
     * @return 
     * @throws IOException 
     */  
    @SuppressWarnings("unchecked")
	public static Map<String, String> parseMap(String jsonStr) throws IOException {
        Map<String, String> map = objectMapper.readValue(jsonStr, Map.class);
        return map;  
    }


    public static Map<String, Object> parseObjMap(String jsonStr) throws IOException {
        Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
        return map;
    }


    public static JsonNode parse(String jsonStr) throws IOException {
        JsonNode node = null;  
        node = objectMapper.readTree(jsonStr);  
        return node;  
    }  
  
    public static ObjectMapper getObjectMapper() {  
        return objectMapper;  
    }

      
    /** 
     * json字符串转 List对象 
     * 2015年4月2日上午10:22:20 
     * auther:shijing 
     * @param str   json字符串 
     * @param clazz 转换的类型 
     * @return 
     */  
    public static <T> List<T> fromListJson(String str,Class<T> clazz){  
        return JSONArray.parseArray(str, clazz);  
    }

    /**
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return 将list对象转换成实体对象list
     */
    public static <T> List<T> transMapListToPojoList(List<Map<String, Object>> map, Class<T> clazz){

            return JSONArray.parseArray(toJson(map),clazz);
    }

    /**
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return 将list对象转换成实体对象list
     */
    public static <T> List<T> transPoListToVoList(Collection<?> map, Class<T> clazz){

        return JSONArray.parseArray(toJson(map),clazz);
    }

    /**
     * Map转实体类
     * @param map 需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
     * @param entity  需要转化成的实体类
     * @return
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) throws Exception {
        T t = null;
            t = entity.newInstance();
            for(Field field : entity.getDeclaredFields()) {
                if (map.containsKey(field.getName())) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    Object object = map.get(field.getName());
                    if (object!= null && field.getType().isAssignableFrom(object.getClass())) {
                        field.set(t, object);
                    }
                    field.setAccessible(flag);
                }
            }
            return t;
    }



}
