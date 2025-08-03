package com.ly.cloud.backup.util;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import org.yaml.snakeyaml.Yaml;


/**
 * @author SYC
 * @Date: 2022/5/16 19:06
 * @Description yml后端格式文件解析工具类
 */
public class YmlUtil {

    //初始化结果集的map
    private static Map<String, Object> resultHashMap =  new HashMap<>();

    /**
     * 从服务器读取配置文件,并初始化ymlMap
     * @param ip
     * @param username
     * @param password
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Map<String, Object> getYml(String ip,String port,String username,String password,String filePath ) throws IOException {
        Yaml yml = new Yaml();
        Connection con = new Connection(ip,Integer.parseInt(port));
        ConnectionInfo connect = con.connect();
        boolean isAuthed = con.authenticateWithPassword(username, password);
        SCPClient scpClient = con.createSCPClient();
        //从服务器获取文件
        final SCPInputStream scpInputStream = scpClient.get(filePath);
        Reader reader = new InputStreamReader(scpInputStream);
        Map map = yml.loadAs(reader, Map.class);
        return map;
    }


    /**
     * 传入想要得到的字段
     * @param map
     * @param name
     * @return
     */
    private static Map<String, Object >  getMapByKey(Map<String, Object> map, String name) {
        //定义结果集
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //拆开map为set集合
        Set<Map.Entry<String, Object>> set = map.entrySet();
        //遍历map
        for (Map.Entry<String, Object> entry : set) {
            //获取map的value值
            Object obj = entry.getValue();
            // 递归结束条件
            //判断map的key是否与条件值相同
            if (entry.getKey().equals(name)) {
                //判断map的value值的类型
                if (obj instanceof Map){
                    return (Map<String, Object>) obj;
                }else if(obj instanceof String || obj instanceof Integer) {
                    resultHashMap.put(String.valueOf(obj).toUpperCase(Locale.ROOT),String.valueOf(obj).toUpperCase(Locale.ROOT));
                }else {
                }
            }

            //判断map的value值是否为map
            if (entry.getValue() instanceof Map) {
                //如果value是Map集合递归
                resultMap = getMapByKey((Map<String, Object>) obj, name);
                //递归的结果如果为空,继续遍历
                if (resultMap == null){
                    continue;
                }
                //不为空返回
                return resultMap;
            }
        }
        return null;
    }

    /**
     * 获取yml后缀格式文件的内容
     * @param ip
     * @param port
     * @param username
     * @param password
     * @param filePath
     * @param findKey
     * @return
     * @throws IOException
     */
    public static List<String> getYmlValues(String ip,String port,String username,String password,String filePath,String findKey) throws IOException {
        getMapByKey(getYml(ip,port,username,password,filePath), findKey);
        List<Object> collect = resultHashMap.values().stream().collect(Collectors.toList());
        List<String> strs = (List<String>)(List)collect;
        resultHashMap.clear();
        return strs;
    }

    public static void main(String[] args) throws IOException {
        String filePath = "/data/share/common/config/properties/temp/ly-bd-mdp-svc-dev.yml";
        List<String> ymlValues = getYmlValues("192.168.35.210", "22", "root", "Ly37621040", filePath, "username");
        System.out.println("ymlValues:"+ymlValues);
    }



}
