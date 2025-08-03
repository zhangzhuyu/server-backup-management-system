package com.ly.cloud.backup.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class SwaggerUtil {

    private String url = "http://localhost:1900/v2/api-docs";

    public static void main(String... args) {
        SwaggerUtil swaggerUtil = new SwaggerUtil();
        swaggerUtil.login();
    }

    public void login() {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                String body = EntityUtils.toString(entity, "UTF-8");
                JSON json = JSON.parseObject(body);
                System.out.println(json.toJSONString());
            }
            EntityUtils.consume(entity);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
