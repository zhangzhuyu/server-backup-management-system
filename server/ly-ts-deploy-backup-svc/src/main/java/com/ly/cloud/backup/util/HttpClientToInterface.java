package com.ly.cloud.backup.util;

import com.ly.cloud.backup.dto.HttpResponseDto;
import com.ly.cloud.backup.exception.ServiceException;
import io.krakens.grok.api.Match;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SYC
 * @Date: 2022/8/23 13:38
 * @Description HttpClient模拟get请求并发送请求参数
 */
public class HttpClientToInterface {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientToInterface.class);
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
     * @param charset
     * @return
     */
    public static String doGet(String url, String charset,String sid){
        logger.info("进入doGet方法url："+url);
        logger.info("进入doGet方法charset："+charset);
        if (StringUtils.isEmpty(charset)){
            charset = "utf-8";
        }
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        BasicClientCookie cookie = new BasicClientCookie("sid", sid);
        cookie.setVersion(0);
        cookie.setDomain("/pms/");   //设置范围
        cookie.setPath("/");
        //执行请求操作，并拿到结果（同步阻塞）


        /**
         * 2.生成GetMethod对象并设置参数
         */
        GetMethod getMethod = new GetMethod(url);
        //设置get请求超时为5秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        //设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
//        getMethod.getParams().setCookiePolicy("Fe26.2**beb2f9a11678c543bcb55f756994d6003863c7aa4bf180a84336a5cdceb9853d*eW3d1NquuJC1qLPDd8KkBw*Wi2WSpwKCOXOrK-zlyhBb0N0-Rh80WnP5UYeo6ZrgfJMzmG5ApSMjMHDJWFSmoK6YOU95BQ94hXIGYqzyP3uykmpq31YuT4a3_OdXu6CfRMBWoQy6U5IBbVPNys6qM_JuxyLnhVvO3aeuskBMHOETS9zq3llv0-AFJioOfZBC9OZwOIP7o67E4baE4kaOwiqy8QKbbqU0phJp6NGKULBVDApShBnr3TQ0_SRCozDjhE**5cbb6c2257cbb8dee7ed393ad3beeea50f66dcc16c993e45de32fd188111b84b*VQ-EZdYtIFqI4FRWvai2AA0cn7XAQ1XT6FjnXV75fjc");
        //执行请求操作，并拿到结果（同步阻塞）
//        org.apache.http.client.CookieStore  cookieStore =new BasicCookieStore();
//        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) cookieStore).build();
////        response = client.execute(httpPost);
//        cookieStore.addCookie(cookie);
//        httpClient.execute(post);//
//        List<Cookie> cookies = cookieStore.getCookies();
//        for (int i = 0; i < cookies.size(); i++) {
//            System.out.println("Local cookie: " + cookies.get(i));
//        }
//        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore((org.apache.http.client.CookieStore) cookieStore).build();
//        response = client.execute(httpGet);
//        String response = "";

        return null;
//        return response;
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
     * @param charset
     * @return
     */
    public static String doGetTryOnlyOne(String url, String charset){
        logger.info("进入doGet方法url："+url);
        logger.info("进入doGet方法charset："+charset);
        if (StringUtils.isEmpty(charset)){
            charset = "utf-8";
        }
        /**
         * 1.生成HttpClient对象并设置参数
         */
        HttpClient httpClient = new HttpClient();
        //设置Http连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
//        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

        /**
         * 2.生成GetMethod对象并设置参数
         */
        GetMethod getMethod = new GetMethod(url);
        //设置get请求超时为5秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
//        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        //设置请求重试处理，用的是默认的重试处理：请求三次
        DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(1,false);
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, defaultHttpMethodRetryHandler);

        String response = "";

        /**
         * 3.执行HTTP GET 请求
         */
        try {
            int statusCode = httpClient.executeMethod(getMethod);

            /**
             * 4.判断访问的状态码
             */
            if (statusCode != HttpStatus.SC_OK){
                System.err.println("请求出错：" + getMethod.getStatusLine());
            }

            /**
             * 5.处理HTTP响应内容
             */
            //HTTP响应头部信息，这里简单打印
            Header[] headers = getMethod.getResponseHeaders();
            for (Header h: headers){
                System.out.println(h.getName() + "---------------" + h.getValue());
            }
            //读取HTTP响应内容，这里简单打印网页内容
            //读取为字节数组
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, charset);
            System.out.println("-----------response:" + response);
            //读取为InputStream，在网页内容数据量大时候推荐使用
            //InputStream response = getMethod.getResponseBodyAsStream();

        } catch (HttpException e) {
            //发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!:"+e.getMessage());
            e.printStackTrace();
        } catch (IOException e){
            //发生网络异常
            System.out.println("发生网络异常!："+e.getMessage());
        }finally {
            /**
             * 6.释放连接
             */
            getMethod.releaseConnection();
        }
        return response;
    }



    /**
     * httpClient使用get获取相应对象
     * 1.得到HttpClient对象
     * 2.创建Httpget对象(发出GET请求，请求的地址)
     * 3.得到响应对象HttpResponse
     * @param uri uri地址
     * @return
     */
    public static HttpResponseDto getHttpResponse(String uri){
        HttpResponseDto httpResponseDto = new HttpResponseDto();
        //1、得到HttpClient对象
        CloseableHttpClient httpclient= HttpClients.createDefault();
        //2、创建Httpget对象(发出GET请求，请求的地址)
        HttpGet httpget=new HttpGet(uri);
        //3、得到响应对象HttpResponse
        try {
            HttpResponse httpResponse = httpclient.execute(httpget);
            //保证http协议头里面有Content-Disposition
            org.apache.http.Header[] allHeaders = httpResponse.getAllHeaders();
            String name1 = "";
                    Iterator<org.apache.http.Header> iterator = Arrays.stream(allHeaders).iterator();
            while (iterator.hasNext()) {
                name1 = iterator.next().getName();
                if (name1.equals("Content-Disposition")) {
                    break;
                }
            }
            if (!name1.equals("Content-Disposition")) {
                return null;
            }
            //获取文件名(url的utf-8解码)
            String head = httpResponse.getHeaders("Content-Disposition")[0].getValue();
            int headStart = head.indexOf("filename=");
            String name = head.substring(headStart + 9);
            String decodedUrl = URLDecoder.decode(name, "UTF-8");
            System.out.println("解密后：" + decodedUrl);
            httpResponseDto.setDecodedUrl(decodedUrl);

            //4、得到实体对象
            HttpEntity entity=httpResponse.getEntity();
            //5、得到文件的InputStream流
            InputStream inputStream = entity.getContent();

            //流先转化为byte数组存储
            try (
                    BufferedInputStream bf = new BufferedInputStream(inputStream);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = bf.read(buffer, 0, 1024))) {
                    bos.write(buffer, 0, len);
                }

                httpResponseDto.setBytes(bos.toByteArray());
                return httpResponseDto;

            } catch (Exception e) {
                throw new RuntimeException("文件下载失败!");
            }
        } catch (IOException e) {
            System.out.println("无法获得请求对象!："+e.getMessage());
        }

        return null;
    }


    /**
     * ZhangZhuYu
     * @param uri uri地址
     * @return
     */
    public static HttpResponseDto getHttpFile(String uri){
        try {
            HttpResponseDto httpResponseDto = downLoadFromUrl(uri);
            System.out.println("文件下载byte数组完成！");
            return httpResponseDto;

        }catch (Exception e){
            throw new ServiceException(e.getMessage());
        }
    }


    /**
     * 从网络Url中下载文件
     * @throws IOException
     */
    private static HttpResponseDto  downLoadFromUrl(String urlStr) throws IOException{
        URL url = new URL(urlStr);
        String file = url.getFile();
        file = file.substring(file.lastIndexOf("/")+1);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] bytes = readInputStream(inputStream);

        HttpResponseDto httpResponseDto = new HttpResponseDto();
        httpResponseDto.setDecodedUrl(file);
        httpResponseDto.setBytes(bytes);
        return httpResponseDto;
    }


    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

































}
