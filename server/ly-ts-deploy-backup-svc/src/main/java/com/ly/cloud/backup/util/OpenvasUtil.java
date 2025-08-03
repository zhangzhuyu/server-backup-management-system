package com.ly.cloud.backup.util;

import com.ly.cloud.backup.dto.OpenvasFailDto;
import com.ly.cloud.backup.dto.OpenvasGetTasksDto;
import com.ly.cloud.backup.dto.OpenvasLoginDto;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class OpenvasUtil {

    public static OpenvasLoginDto login(String host, String username, String password) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, MalformedCookieException, URISyntaxException {
        CookieStore httpCookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(httpCookieStore);
        context.setAttribute(ClientContext.COOKIE_STORE, httpCookieStore);
        try (CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultCookieStore(httpCookieStore)
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .setRetryHandler(new DefaultHttpMethodRetryHandler())
                .build();){
            URI uri = new URIBuilder("https://"+host+"/gmp")
                    .build();
            HttpPost request = new HttpPost(uri);
            List<NameValuePair> paramsList = new ArrayList<>();
            paramsList.add(new BasicNameValuePair("cmd", "login"));
            paramsList.add(new BasicNameValuePair("login", username));
            paramsList.add(new BasicNameValuePair("password", password));
            request.setEntity(new UrlEncodedFormEntity(paramsList, StandardCharsets.UTF_8));
            HttpResponse response = httpClient.execute(request,context);
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode>=300||statusCode<200){
                XStream xstream = new XStream(new DomDriver());
                xstream.processAnnotations(OpenvasFailDto.class);
                OpenvasFailDto dto= (OpenvasFailDto) xstream.fromXML( response.getEntity().getContent());
                System.out.println("请求错误："+statusCode+" "+dto.getGsad_response().getTitle()+" "+dto.getGsad_response().getMessage());
                OpenvasLoginDto d=new OpenvasLoginDto();
                d.setMassage(dto.getGsad_response().getTitle()+" "+dto.getGsad_response().getMessage());
                return d;
            }else{
                BrowserCompatSpec sp=new BrowserCompatSpec(){
                    @Override
                    public boolean match(Cookie cookie, CookieOrigin origin) {
                        return true;
                    }
                };
//                DefaultCookieSpec sp=new DefaultCookieSpec();//org.apache.http.cookie.MalformedCookieException: Invalid 'expires' attribute: Tue, 18 Apr 2023 08:33:49 GMT
                List<Cookie>cookies=sp.parse(response.getFirstHeader("Set-Cookie"),new CookieOrigin(uri.getHost(),uri.getPort(), "/", true));
                Cookie sid=cookies.stream().filter(t->t.getName().equals("GSAD_SID")).findAny().orElse(new BasicClientCookie("A",""));

                XStream xstream = new XStream(new DomDriver());
                xstream.processAnnotations(OpenvasLoginDto.class);
                OpenvasLoginDto dto= (OpenvasLoginDto) xstream.fromXML( response.getEntity().getContent());
                dto.setSid(sid);
                return dto;
            }
        }
    }
    public static OpenvasGetTasksDto getTasks(String host, String username, String password) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, MalformedCookieException, URISyntaxException {
        OpenvasLoginDto loginDto=login(host,username,password);
        CookieStore httpCookieStore = new BasicCookieStore();
        httpCookieStore.addCookie(loginDto.getSid());

        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(httpCookieStore);
        context.setAttribute(ClientContext.COOKIE_STORE, httpCookieStore);
        try (CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultCookieStore(httpCookieStore)
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .setRetryHandler(new DefaultHttpMethodRetryHandler())
                .build();){
            URI uri = new URIBuilder("https://"+host+"/gmp")
                    .addParameter("token", loginDto.getToken())
                    .addParameter("cmd", "get_tasks")
                    .addParameter("usage_type", "scan")
                    .addParameter("filter", "sort=name first=1 rows=10")
                    .build();
            HttpGet request = new HttpGet(uri);
            HttpResponse response = httpClient.execute(request,context);
            int statusCode = response.getStatusLine().getStatusCode();

            if(statusCode>=300||statusCode<200){
                XStream xstream = new XStream(new DomDriver());
                xstream.processAnnotations(OpenvasFailDto.class);
                OpenvasFailDto dto= (OpenvasFailDto) xstream.fromXML( response.getEntity().getContent());
                System.out.println("请求错误："+statusCode+" "+dto.getGsad_response().getTitle()+" "+dto.getGsad_response().getMessage());
//                OpenvasLoginDto d=new OpenvasLoginDto();
//                d.setMassage(dto.getGsad_response().getTitle()+" "+dto.getGsad_response().getMessage());
                return null;
            }else{
                XStream xstream = new XStream(new DomDriver());
                xstream.ignoreUnknownElements();
                xstream.processAnnotations(OpenvasGetTasksDto.class);
                OpenvasGetTasksDto dto= (OpenvasGetTasksDto) xstream.fromXML( response.getEntity().getContent());
//                System.out.println(new String(IOUtils.toByteArray(response.getEntity().getContent())));
                return dto;
            }
        }
    }
}
