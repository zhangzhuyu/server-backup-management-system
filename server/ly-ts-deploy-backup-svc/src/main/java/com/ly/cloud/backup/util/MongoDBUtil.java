package com.ly.cloud.backup.util;

import com.ly.cloud.backup.exception.ServiceException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.Collections;

/**
 * @author zhangzhuyu
 * @Date: 2023/11/10
 * @Description
 */
public class MongoDBUtil {

    //测试mongodb的url连接是否有效，如果无效会抛出异常
    public static void MongoDBConnectTest(String ip, String port, String user, String password){
        MongoClient client = null;
        try {
            String database = "admin";
            //创建MongoCredential对象
            MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
            //创建MongoClientOptions对象，并设置连接超时时间
            MongoClientOptions options = MongoClientOptions.builder()
                    .connectTimeout(5000)
                    .build();
            //创建MongoClient对象
            client = new MongoClient(new ServerAddress(ip, Integer.parseInt(port)), Collections.singletonList(credential), options);
            //获取MongoDatabase对象
            MongoDatabase db = client.getDatabase(database);
            db.getCollection("test").find().iterator();
            //打印连接状态
            System.out.println("Connected to MongoDB!");

        }catch (Exception e){
            System.out.println("disconnected to MongoDB!");
            throw new ServiceException("无效的mongodb链接:"+e.getMessage());
        }finally {
            //关闭MongoClient连接
            assert client != null;
            client.close();
        }
    }

}



















































