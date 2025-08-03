package com.ly.cloud.backup.util.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author SYC
 * @Date: 2023/3/8 9:59
 * @Description url编码工具类
 */
public class EncoderUtil {

    private static Logger logger = LoggerFactory.getLogger(EncoderUtil.class);

    public static String urlEncode(String urlToken) {
        String encoded = null;
        try {
            //用URLEncoder.encode方法会把空格变成加号（+）,encode之后在替换一下
            encoded = URLEncoder.encode(urlToken, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            logger.error("URLEncode error {}", e);
        }
        return encoded;
    }

    public static String urlDecode(String urlToken) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(urlToken, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("URLEncode error {}", e);
        }
        return decoded;
    }

    public static void main(String[] args) {
//        String requestUrl = "http://192.168.35.202:5601/api/apm/traces/6f86fee4e0fc77568f1416d7b2c889f5?start=2023-03-06T12:01:00.000Z&end=2023-03-06T12:16:48.499Z";
//        http://192.168.35.202:5601/api/apm/traces/6f86fee4e0fc77568f1416d7b2c889f5?start=2023-03-06T12%3A01%3A00.000Z&end=2023-03-06T12%3A16%3A48.499Z
       String sCurrent = "2023-03-06T12:01:00.000Z";
        System.out.println("sCurrent:"+sCurrent);
        String s = urlEncode(sCurrent);
//        2023-03-06T12%3A01%3A00.000Z
        System.out.println("s编码:"+s);
        String sDecode = urlDecode("2023-03-06T12:01:00.000Z");
        System.out.println("s解码:"+sDecode);
    }
}
