package com.ly.cloud.backup.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DruidUtil {

    //    private String loginUrl = "http://192.168.35.105:1400/druid/submitLogin";
    private String loginUrl = "http://192.168.3.59:1900/druid/submitLogin";

    //    private String sqlUrl = "http://192.168.35.105:1400/druid/sql.json?orderBy=MaxTimespan&orderType=desc&page=1&perPageCount=20";
    private String sqlUrl = "http://192.168.3.59:1900/druid/sql.json?orderBy=MaxTimespan&orderType=desc&page=1&perPageCount=20";

    public static void main(String... args) {
        DruidUtil druidUtil = new DruidUtil();
        druidUtil.login();
    }

    public void login() {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(loginUrl);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("loginUsername", "admin"));
            params.add(new BasicNameValuePair("loginPassword", "admin"));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(httpResponse.getEntity());
                if ("success".equals(body)) {
                    HttpGet httpGet = new HttpGet(sqlUrl);
                    httpResponse = httpclient.execute(httpGet);
                    entity = httpResponse.getEntity();
                    if (entity != null) {
                        body = EntityUtils.toString(entity, "UTF-8");
                        JSON json = JSON.parseObject(body);
                        System.out.println(json.toJSONString());
                    }
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
