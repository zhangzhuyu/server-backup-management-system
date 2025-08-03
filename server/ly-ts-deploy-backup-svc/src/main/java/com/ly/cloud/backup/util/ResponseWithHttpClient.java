package com.ly.cloud.backup.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ly.cloud.backup.service.SystemSetupService;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;

public class ResponseWithHttpClient {

    private static final Logger log = LoggerFactory.getLogger(ResponseWithHttpClient.class);

    private static MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
    private static int connectionTimeOut = 20000;//设置连接超时的时间
    private static int socketTimeOut = 10000;//设置读取数据超时的时间
    private static int MaxtotalConnections = 40;//总的连接数
    private static int MaxConnectionsPerHost = 35;//每个host最大的连接数

    private static boolean mark = false;


    @Autowired
    private SystemSetupService systemSetupService;

    private static final String KIBANA_VERSION_7_17_5 = "7.17.5";

    //用来存储cookies信息的变量
    private CookieStore store;

    public static void SetPara() {
        manager.getParams().setConnectionTimeout(connectionTimeOut);
        manager.getParams().setSoTimeout(socketTimeOut);
        manager.getParams().setDefaultMaxConnectionsPerHost(MaxConnectionsPerHost);
        manager.getParams().setMaxTotalConnections(MaxtotalConnections);
        mark = true;
    }

    private static final String kibanaVersionTest = "7.17.5";

    /**
     * 发送post请求
     *
     * @param url     地址
     * @param encode  默认utf-8
     * @param content {"username":"haha"}
     * @return
     */
    public static String getResponseWithHttpClient(String url, String encode, Map<String, String> content) {
        HttpClient client = new HttpClient(manager);
        if (!mark) {
            ResponseWithHttpClient.SetPara();
        }

        PostMethod postMethod = new PostMethod(url);
        //传参数
        if (content != null && content.size() > 0) {
            int index = 0;
            NameValuePair[] nvArray = new NameValuePair[content.size()];
            for (Map.Entry<String, String> entry : content.entrySet()) {
                nvArray[index] = new NameValuePair(entry.getKey(), entry.getValue());
                index++;
            }
            postMethod.setRequestBody(nvArray);
        }
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encode);

        String result = null;
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            client.executeMethod(postMethod);

            reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet()));
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                buffer.append(inputLine);
            }

            result = buffer.toString();
            result = ResponseWithHttpClient.ConverterStringCode(buffer.toString(), postMethod.getResponseCharSet(), encode);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
        	postMethod.releaseConnection();
            try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

    }

    private static String ConverterStringCode(String source,String srcEncode, String destEncode) {
        if (source != null) {
            try {
                return new String(source.getBytes(srcEncode), destEncode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 钉钉调用，必须要采用JSON传参
     * @param url
     * @param jsonObject
     * @param encoding
     * @return
     * @throws ParseException
     * @throws IOException
     */
    public static String send(String url, JSONObject jsonObject, String encoding) throws ParseException, IOException{
        String body = "";

        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                "application/json"));
        //设置参数到请求对象中
        httpPost.setEntity(s);
        System.out.println("请求地址："+url);
//        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }


    /**
     * 获取taier的对象信息
     */
    /*private SystemSetupPo taierInfo() {
        return systemSetupService.getOne(new QueryWrapper<SystemSetupPo>().lambda().eq(SystemSetupPo::getSetKey, TAIER));
    }

    private SystemSetupPo systemSetupPo = taierInfo();*/






    public static boolean sendTest() throws ParseException, IOException{
        return true;
    }



    //taier post风格 form-data形式传参   获取返回的json body
    public JSONObject doPostTaierResponse(String finalUrl, Map<String, String> params,String cookie) {
        CloseableHttpResponse response = null;
        String value2=null;

        try {
//        String loginUrlResult = "";
//            PostMethod postMethod = new PostMethod("192.168.35.210");
//        if (username != null && password != null) {
            //String loginUrl = "http://{0}:{1}/taier/api/user/login";
            //String format = MessageFormat.format(loginUrl, url, port);
            //logger.info("format:"+format);

//            loginUrlResult = ResponseWithHttpClient.doPostTaier(format, map);
//            logger.info("loginUrlResult:"+loginUrlResult);


//        String loginUrlResult = ResponseWithHttpClient.doPostTaier(format, map);

            //创建httpclient对象
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(finalUrl);



//            params.
//            HttpParams httpParams =  HttpParams;
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                System.out.println("next:"+next);
                String key = next.getKey();
                System.out.println("key:"+key);
                String value = next.getValue();
                System.out.println("value:"+value);
//                postMethod.addParameter(key, value);
                httpPost.setHeader(key,value);
                params.put(key,value);
//                httpParams.setParameter(key,value);
                //            httpPost.set
            }

            // 创建参数列表
//            if (param != null) {
//                List<NameValuePair> paramList = new ArrayList<>();
//                for (String key : param.keySet()) {
//                    paramList.add(new BasicNameValuePair(key, param.get(key)));
//                }
            // 模拟表单
//                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
//                httpPost.setEntity(entity);
//            }
            // 执行http请求


            String body1="";
            List<org.apache.http.NameValuePair> paramList = new ArrayList<>();
            for (String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(paramList,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            httpPost.setEntity(entity);
//            entity.setContentType("multipart/form-data");
            //装填参数
//            StringEntity s = new StringEntity(params.toString(), "utf-8");
//            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
//                    "application/json"));
            //设置参数到请求对象中
//            httpPost.setEntity(s);
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
//            httpPost.setHeader("Content-type", "multipart/form-data; boundary=<calculated when request is sent>");
//            httpPost.setHeader("Content-type", "multipart/form-data; boundary=<calculated when request is sent>");
//            httpPost.setHeader("Content-Length","<calculated when request is sent>");
//            httpPost.setHeader("Host","192.168.35.210");
//            httpPost.setHeader("Host","<calculated when request is sent>");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("Cookies",cookie);
            org.apache.http.Header[] allHeaders = httpPost.getAllHeaders();
            for (org.apache.http.Header allHeader : allHeaders) {
                System.out.println("allHeader:"+allHeader.getName()+","+allHeader.getValue());
            }
//        httpPost.setHeader("kbn-version",kbnVersion);
//            httpPost.setHeader("kbn-version","7.13.4");
            //执行请求操作，并拿到结果（同步阻塞）
            System.out.println("httpPost:"+httpPost);
            System.out.println("httpPost.getEntity():"+httpPost.getEntity());

            try {

                // 获取cookies信息
                store = (CookieStore) new BasicCookieStore();
                CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) store).build();

//                org.apache.http.client.CookieStore  cookieStore =new BasicCookieStore();
//                CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                response = client.execute(httpPost);
//                response = client1.execute(postMethod);
                System.out.println("response:"+response);
                HttpEntity entity1 = response.getEntity();
                System.out.println("entity1:"+entity1);
                String sResponse= EntityUtils.toString(entity1, "UTF-8");
                System.out.println("Post 返回结果sResponse:"+sResponse);

                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");

                for (org.apache.http.Header header : headers) {
                    String name1 = header.getName();
                    String value1 = header.getValue();
                    System.out.println("name:"+name1);
                    System.out.println("value:"+value1);
                }
                //读取cookie信息
                List<HttpCookie> cookielist = store.getCookies();
                System.out.println("cookielist:"+cookielist);
                for(HttpCookie cookiee: cookielist){
                    String name2=cookiee.getName();
                    value2=cookiee.getValue();
                    System.out.println("cookie name =" + name2);
                    System.out.println("Cookie value=" + value2);
                }
                System.out.println(JSON.parseObject(sResponse));
                return JSON.parseObject(sResponse);


  /*              // 获得cookie并存取
                List<Cookie> cookies = cookieStore.getCookies();
                System.out.println("cookies:"+cookies);
                for(Cookie cookie:cookies){
                    String value = cookie.getValue();
                    System.out.println("cookies-value:"+value);
                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("body1:"+body1);
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        }finally {
            //释放链接
//            assert response != null;
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            WebResponse<?> webResponse = taierController.listTenant();
        }
        return null;
    }



    //taier post风格 form-data形式传参   获取Cookies里的token
    public String doPostTaierCookies(String finalUrl, Map<String, String> params) {
        CloseableHttpResponse response = null;
        String value2=null;

        try {
//        String loginUrlResult = "";
//            PostMethod postMethod = new PostMethod("192.168.35.210");
//        if (username != null && password != null) {
            //String loginUrl = "http://{0}:{1}/taier/api/user/login";
            //String format = MessageFormat.format(loginUrl, url, port);
            //logger.info("format:"+format);

//            loginUrlResult = ResponseWithHttpClient.doPostTaier(format, map);
//            logger.info("loginUrlResult:"+loginUrlResult);


//        String loginUrlResult = ResponseWithHttpClient.doPostTaier(format, map);

            //创建httpclient对象
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(finalUrl);



//            params.
//            HttpParams httpParams =  HttpParams;
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                System.out.println("next:"+next);
                String key = next.getKey();
                System.out.println("key:"+key);
                String value = next.getValue();
                System.out.println("value:"+value);
//                postMethod.addParameter(key, value);
                httpPost.setHeader(key,value);
                params.put(key,value);
//                httpParams.setParameter(key,value);
                //            httpPost.set
            }

            // 创建参数列表
//            if (param != null) {
//                List<NameValuePair> paramList = new ArrayList<>();
//                for (String key : param.keySet()) {
//                    paramList.add(new BasicNameValuePair(key, param.get(key)));
//                }
            // 模拟表单
//                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList,"utf-8");
//                httpPost.setEntity(entity);
//            }
            // 执行http请求


            String body1="";
            List<org.apache.http.NameValuePair> paramList = new ArrayList<>();
            for (String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key)));
            }
            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(paramList,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            httpPost.setEntity(entity);
//            entity.setContentType("multipart/form-data");
            //装填参数
//            StringEntity s = new StringEntity(params.toString(), "utf-8");
//            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
//                    "application/json"));
            //设置参数到请求对象中
//            httpPost.setEntity(s);
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
//            httpPost.setHeader("Content-type", "multipart/form-data; boundary=<calculated when request is sent>");
//            httpPost.setHeader("Content-type", "multipart/form-data; boundary=<calculated when request is sent>");
//            httpPost.setHeader("Content-Length","<calculated when request is sent>");
//            httpPost.setHeader("Host","192.168.35.210");
//            httpPost.setHeader("Host","<calculated when request is sent>");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            org.apache.http.Header[] allHeaders = httpPost.getAllHeaders();
            for (org.apache.http.Header allHeader : allHeaders) {
                System.out.println("allHeader:"+allHeader.getName()+","+allHeader.getValue());
            }
//        httpPost.setHeader("kbn-version",kbnVersion);
//            httpPost.setHeader("kbn-version","7.13.4");
            //执行请求操作，并拿到结果（同步阻塞）
            System.out.println("httpPost:"+httpPost);
            System.out.println("httpPost.getEntity():"+httpPost.getEntity());

            try {

                // 获取cookies信息
                store = (CookieStore) new BasicCookieStore();
                CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) store).build();

//                org.apache.http.client.CookieStore  cookieStore =new BasicCookieStore();
//                CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
                response = client.execute(httpPost);
//                response = client1.execute(postMethod);
                System.out.println("response:"+response);
                HttpEntity entity1 = response.getEntity();
                System.out.println("entity1:"+entity1);
                String sResponse= EntityUtils.toString(entity1, "UTF-8");
                System.out.println("Post 返回结果sResponse:"+sResponse);

                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");

                for (org.apache.http.Header header : headers) {
                    String name1 = header.getName();
                    String value1 = header.getValue();
                    System.out.println("name:"+name1);
                    System.out.println("value:"+value1);
                }
                //读取cookie信息
                List<HttpCookie> cookielist = store.getCookies();
                System.out.println("cookielist:"+cookielist);
                for(HttpCookie cookie: cookielist){
                    String name2=cookie.getName();
                    value2=cookie.getValue();
                    System.out.println("cookie name =" + name2);
                    System.out.println("Cookie value=" + value2);
                }
                return value2;


  /*              // 获得cookie并存取
                List<Cookie> cookies = cookieStore.getCookies();
                System.out.println("cookies:"+cookies);
                for(Cookie cookie:cookies){
                    String value = cookie.getValue();
                    System.out.println("cookies-value:"+value);
                }*/

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("body1:"+body1);
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        }finally {
            //释放链接
//            assert response != null;
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            WebResponse<?> webResponse = taierController.listTenant();
        }
        return value2;
    }


    public static String doPost(String url, Map<String, String> params) {
    	 HttpClient client = new HttpClient(manager);
         if (!mark) {
             ResponseWithHttpClient.SetPara();
         }
         PostMethod postMethod = new PostMethod(url);
         postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;



         if(params != null){
             List<NameValuePair> data = new ArrayList<NameValuePair>();
             Set<String> keys = params.keySet();
             for(Iterator<String> it = keys.iterator(); it.hasNext();) {
                 String key = it.next();
                 String value = params.get(key);
                 NameValuePair p = new NameValuePair(key, value);
                 data.add(p);
             }
             postMethod.setRequestBody(data.toArray(new NameValuePair[]{}));
         }

         
         String result = null;
         StringBuffer buffer = new StringBuffer();
         BufferedReader reader = null;
         try {
             client.executeMethod(postMethod);

             reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet()));
             String inputLine = null;
             while ((inputLine = reader.readLine()) != null) {
                 buffer.append(inputLine);
             }

             result = buffer.toString();
             result = ResponseWithHttpClient.ConverterStringCode(buffer.toString(), postMethod.getResponseCharSet(), "utf-8");
             return result;
         } catch (Exception e) {
             throw new RuntimeException(e.getMessage(), e);
         } finally {
         	postMethod.releaseConnection();
             try {
 				reader.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
         }
    }

    //读取Cookie的序列化文件，读取后可以直接使用
    private CookieStore readCookieStore( String savePath ) throws IOException, ClassNotFoundException {
        FileInputStream fs = new FileInputStream("cookie");//("foo.ser");
        ObjectInputStream ois = new ObjectInputStream(fs);
        CookieStore cookieStore = (CookieStore) ois.readObject();
        ois.close();
        return cookieStore;

    }

    public static String sendJsonGetCookies(String url, JSONObject jsonObject,String encoding,String kbnVersion) throws ParseException, IOException, ClassNotFoundException {
        if (StringUtils.isEmpty(encoding)){
            encoding="utf-8";
        }
        String body = "";
        CloseableHttpResponse response = null;
        try {
            //创建httpclient对象
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            //装填参数
            StringEntity s = new StringEntity(jsonObject.toString(), encoding);
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            //设置参数到请求对象中
            httpPost.setEntity(s);
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("kbn-version",kbnVersion);
//            httpPost.setHeader("kbn-version","7.13.4");
            //执行请求操作，并拿到结果（同步阻塞）
            org.apache.http.client.CookieStore  cookieStore =new BasicCookieStore();
            CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = client.execute(httpPost);
            log.info("response:"+response.toString());
            // 获得cookie并存取
            List<Cookie> cookies = cookieStore.getCookies();
            for(Cookie cookie:cookies){
                body = cookie.getValue();
            }
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }finally {
            //释放链接
            if (response !=null){
                response.close();
            }
        }
        return body;
    }


    public static String sendJsonPost(String url, JSONObject jsonObject,String encoding,String kbnVersion) throws ParseException, IOException, ClassNotFoundException {
        String body = "";
        CloseableHttpResponse response = null;
        try {
            //创建httpclient对象
//        CloseableHttpClient client = HttpClients.createDefault();
            //创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);

            //装填参数
            StringEntity s = new StringEntity(jsonObject.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            //设置参数到请求对象中
            httpPost.setEntity(s);
            System.out.println("请求地址："+url);
//        System.out.println("请求参数："+nvps.toString());
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
//        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            httpPost.setHeader("kbn-version",kbnVersion);
            //执行请求操作，并拿到结果（同步阻塞）
            org.apache.http.client.CookieStore  cookieStore =new BasicCookieStore();
            CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) cookieStore).build();
            response = client.execute(httpPost);

            //获取结果实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
            //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, encoding);
            }
            EntityUtils.consume(entity);
        } catch (UnsupportedCharsetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }finally {
            //释放链接
            response.close();
        }
        System.out.println("结果值body:"+body);
        return body;
    }


    /**
     * httpClient的get请求方式
     * 使用GetMethod来访问一个URL对应的网页实现步骤：
     * 1.生成一个HttpClient对象并设置相应的参数；
     * 2.生成一个GetMethod对象并设置响应的参数；
     * 3.用HttpClient生成的对象来执行GetMethod生成的Get方法；
     * 4.处理响应状态码；
     * 5.若响应正常，处理HTTP响应内容；
     * 6.释放连接。
     * @param url
     * @param encoding
     * @return
     */
    public static String doGet(String url, String encoding,String sid,String domain) throws IOException {
        String body="";
        log.info("进入doGet方法url："+url);
        log.info("进入doGet方法charset："+encoding);
        if (StringUtils.isEmpty(encoding)){
            encoding = "utf-8";
        }
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        org.apache.http.client.CookieStore cookieStore =new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("sid", sid);
        cookie.setVersion(0);
        cookie.setDomain(domain);   //设置范围
        cookie.setPath("/");
        cookie.setSecure(false);
        cookieStore.addCookie(cookie);
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        //创建post方式请求对象
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        response = client.execute(httpGet);
        // 获取cookies信息
//        List<Cookie> cookies = cookieStore.getCookies();
//        for(Cookie cookie1:cookies){
//            String name = cookie1.getName();
//            String value = cookie1.getValue();
//            System.out.println("cookies key ="+name+",cookies value ="+value);
//        }
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
           body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        return body;
    }


    /**
     * httpClient的get请求方式
     * 使用GetMethod来访问一个URL对应的网页实现步骤：
     * 1.生成一个HttpClient对象并设置相应的参数；
     * 2.生成一个GetMethod对象并设置响应的参数；
     * 3.用HttpClient生成的对象来执行GetMethod生成的Get方法；
     * 4.处理响应状态码；
     * 5.若响应正常，处理HTTP响应内容；
     * 6.释放连接。
     * @param url
     * @param encoding
     * @return
     */
    public static String doGet(String url, String encoding,String sid,String domain,String kibanaVersion) throws IOException {

        //定义的kibana版本(7.17.5)
        int kibanaVersionDefineReplace = UsualUtil.getInt(kibanaVersionTest.replace(".",""));
        //系统当前的kibana版本
        int kibanaVersionSystemReplace = UsualUtil.getInt(kibanaVersion.replace(".",""));
        String environment = "environment";
        String kuery = "kuery";
        //给url添加7.17.5版本特有的请求参数
        if (kibanaVersionSystemReplace >= kibanaVersionDefineReplace){
            if (StringUtils.isNotEmpty(url) && !url.contains(environment)){
                url =  url+"&environment=ENVIRONMENT_ALL";
            }
            if (StringUtils.isNotEmpty(url) && !url.contains(kuery)){
                url =  url+"&kuery=";
            }
        }
        String body="";
        log.info("进入doGet方法url："+url);
        log.info("进入doGet方法charset："+encoding);
        if (StringUtils.isEmpty(encoding)){
            encoding = "utf-8";
        }
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        org.apache.http.client.CookieStore cookieStore =new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("sid", sid);
        cookie.setVersion(0);
        cookie.setDomain(domain);   //设置范围
        cookie.setPath("/");
        cookie.setSecure(false);
        cookieStore.addCookie(cookie);
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        //创建post方式请求对象
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        response = client.execute(httpGet);
        // 获取cookies信息
//        List<Cookie> cookies = cookieStore.getCookies();
//        for(Cookie cookie1:cookies){
//            String name = cookie1.getName();
//            String value = cookie1.getValue();
//            System.out.println("cookies key ="+name+",cookies value ="+value);
//        }
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        return body;
    }


    /**
     * 以post方式调用第三方接口,以form-data 形式  发送数据  返回的是cookies
     *
     * @return
     * @Param url 指定的链接
     * @Param paramMap 写入form-data的数据
     */
    public String doTaierPostFormDataCookie(String url,Map<String, String> paramMap){
        // 创建Http实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost实例
        HttpPost httpPost = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            //表单中参数
            for(Map.Entry<String, String> entry: paramMap.entrySet()) {
                builder.addPart(entry.getKey(),new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
            }

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                // 返回
                String res = EntityUtils.toString(response.getEntity(), java.nio.charset.Charset.forName("UTF-8"));
                System.out.println(res);

                //下面是提取 post请求的cookies
                StringBuilder myCookies = new StringBuilder();
                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
                for (int i = 0; i <headers.length ; i++) {
                    String cookie = headers[i].toString().substring(11, headers[i].toString().indexOf(";")+1);
                    myCookies.append(cookie);
                    System.out.println(myCookies);
                }

                return myCookies.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("调用HttpPost失败！" + e.toString());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    System.out.println("关闭HttpPost连接失败！");
                }
            }
        }
        return null;
    }



    /**
     * 以post方式调用第三方接口,以form-data 形式  发送数据  返回的是body
     *
     * @return
     * @Param url 指定的链接
     * @Param paramMap 写入form-data的数据
     */
    public JSONObject doTaierPostFormData(String url,Map<String, String> paramMap){
        // 创建Http实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost实例
        HttpPost httpPost = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(java.nio.charset.Charset.forName("UTF-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            //表单中参数
            for(Map.Entry<String, String> entry: paramMap.entrySet()) {
                builder.addPart(entry.getKey(),new StringBody(entry.getValue(), ContentType.create("text/plain", Consts.UTF_8)));
            }

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交

            if (response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                // 返回
                String res = EntityUtils.toString(response.getEntity(), java.nio.charset.Charset.forName("UTF-8"));
                System.out.println(res);

                //下面是提取 post请求的cookies
                StringBuilder myCookies = new StringBuilder();
                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
                for (int i = 0; i <headers.length ; i++) {
                    String cookie = headers[i].toString().substring(11, headers[i].toString().indexOf(";")+1);
                    myCookies.append(cookie);
                    System.out.println(myCookies);
                }

                return JSON.parseObject(res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("调用HttpPost失败！" + e.toString());
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    System.out.println("关闭HttpPost连接失败！");
                }
            }
        }
        return null;
    }


    /*taier接口  get请求方式 返回json数据*/
    /*param 为表单传参  ?xxx=xxx&*/
    public JSONObject doTaierGet(String url,Map<String,String> param,String cookie) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key)); }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            //添加将cookie添加至请求头
            httpGet.addHeader("Cookie", cookie);


            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

            //下面是提取 post请求的cookies
            StringBuilder myCookies = new StringBuilder();
            org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
            for (int i = 0; i <headers.length ; i++) {
                String cookiee = headers[i].toString().substring(11, headers[i].toString().indexOf(";")+1);
                myCookies.append(cookiee);
                System.out.println(myCookies);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(resultString);
        return JSON.parseObject(resultString);
    }



    /*taier接口  get请求方式 返回Cookies*/
    /*param 为表单传参  ?xxx=xxx&*/
    public String doTaierGetCookies(String url,Map<String,String> param,String cookie) throws IOException {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        StringBuilder myCookies = new StringBuilder();
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key)); }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            //添加将cookie添加至请求头
            httpGet.addHeader("Cookie", cookie);


            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

            //下面是提取 post请求的cookies
            org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
            for (int i = 0; i <headers.length ; i++) {
                String cookiee = headers[i].toString().substring(11, headers[i].toString().indexOf(";")+1);
                myCookies.append(cookiee);
                System.out.println(myCookies);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(resultString);
        return myCookies.toString();
    }


    /*taier接口  传入参数为json的post请求*/
    public JSONObject doTaierPost(String param,String url,String token){
        JSONObject jsonObject = new JSONObject();
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(url);
            JSONObject jsonString = JSON.parseObject(param);
            //设置请求体参数
            StringEntity entity = new StringEntity(param);
            entity.setContentEncoding("utf-8");
            httpPost.setEntity(entity);
            //设置请求头部
            httpPost.setHeader("Content-Type", "application/json");
            if(token != null && !"".equals(token)){
                httpPost.setHeader("Cookie",token);
            }
            //执行请求，返回请求响应
            try (final CloseableHttpResponse response = httpClient.execute(httpPost)) {
                //请求返回状态码
                int statusCode = response.getStatusLine().getStatusCode();
                //请求成功
                if (statusCode == HttpStatus.SC_OK && statusCode <= HttpStatus.SC_TEMPORARY_REDIRECT) {
                    //取出响应体
                    final HttpEntity entity2 = response.getEntity();
                    //从响应体中解析出token
                    String responseBody = EntityUtils.toString(entity2, "utf-8");
                    jsonObject = JSONObject.parseObject(responseBody);
                    //token = jsonObject.getString("access_token");
                } else {
                    //请求失败
                    throw new ClientProtocolException("请求失败，响应码为：" + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject);
        return jsonObject;
    }



    /*taier接口  传入参数为json的post请求  返回cookies*/
    public String doTaierPostCookies(String param,String url,String token){
        JSONObject jsonObject = new JSONObject();
        StringBuilder myCookies = new StringBuilder();
        try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpPost httpPost = new HttpPost(url);
            JSONObject jsonString = JSON.parseObject(param);
            //设置请求体参数
            StringEntity entity = new StringEntity(param);
            entity.setContentEncoding("utf-8");
            httpPost.setEntity(entity);
            //设置请求头部
            httpPost.setHeader("Content-Type", "application/json");
            if(token != null && !"".equals(token)){
                httpPost.setHeader("Cookie",token);
            }
            //执行请求，返回请求响应
            try (final CloseableHttpResponse response = httpClient.execute(httpPost)) {
                //请求返回状态码
                int statusCode = response.getStatusLine().getStatusCode();
                //请求成功
                if (statusCode == HttpStatus.SC_OK && statusCode <= HttpStatus.SC_TEMPORARY_REDIRECT) {
                    //取出响应体
                    final HttpEntity entity2 = response.getEntity();
                    //从响应体中解析出token
                    String responseBody = EntityUtils.toString(entity2, "utf-8");
                    jsonObject = JSONObject.parseObject(responseBody);
                    //token = jsonObject.getString("access_token");
                } else {
                    //请求失败
                    throw new ClientProtocolException("请求失败，响应码为：" + statusCode);
                }

                //下面是提取 post请求的cookies
                org.apache.http.Header[] headers = response.getHeaders("Set-Cookie");
                for (int i = 0; i <headers.length ; i++) {
                    String cookiee = headers[i].toString().substring(11, headers[i].toString().indexOf(";")+1);
                    myCookies.append(cookiee);
                    System.out.println(myCookies);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject);
        return myCookies.toString();
    }




    //publicKey=2019-09-09 02:59:42, appId=69C1BB8E-C858-4FB3-467B-24007F239743, authString=662884f33a380158970d11b63905c21a
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//    	String url = "http://192.168.35.202:5601/internal/security/login";
//    	Map<String, String> params = new java.util.HashMap<>();
//    	params.put("appId", "69C1BB8E-C858-4FB3-467B-24007F239743");
//    	params.put("publicKey", "2019-00-09 02:09:08");
//    	params.put("authString", "c86300f9c6fae8a5f1033bc0e801dc35");
//    	String r = doPost(url, params);
//    	System.out.println(r);

    	String url ="http://192.168.35.202:5601/internal/security/login";
        String jsonSting  ="{\"currentURL\": \"http://192.168.35.202:5601/login?msg=LOGGED_OUT\",\"params\": " +
                "{\"username\": \"elastic\", \"password\": \"Ly37621040\"},\"providerName\": \"basic\"," +
                "\"providerType\": \"basic\"}";
        JSONObject jsonObject = JSONObject.parseObject(jsonSting);
        String encoding = "utf-8";
        String sout = sendJsonGetCookies(url, jsonObject, encoding,"7.13.4");
        System.out.println("-------------------------------sout登录:"+sout);

        String requestUrl = "http://192.168.35.202:5601/api/apm/traces/6f86fee4e0fc77568f1416d7b2c889f5?start=2023-03-06T12:01:00.000Z&end=2023-03-06T12:16:48.499Z";
        String requestUrlResult = HttpClientToInterface.doGet(requestUrl, encoding,sout);
//        String requestUrlResult = sendJson(requestUrl, jsonObject, encoding);
        System.out.println("----------------requestUrlResult:"+requestUrlResult);
//        json.put("params",{"username": "elastic", "password": "Ly37621040"});
//        json.put("code",req.getCode());
//        json.put("remote_ip",req.getRemoteIp());
//        json.put("redirect_uri",req.getRedirectUri());
//        sendJson(url)

    }

}
