package com.ly.cloud.dingtalk.util;

import com.aliyun.teaopenapi.models.Config;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiV2UserGetbymobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiV2UserGetbymobileResponse;
import com.ly.cloud.dingtalk.constant.UrlConstant;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 获取access_token工具类
 *
 * @author admin
 */
@Slf4j
@Component
public class AccessTokenUtil {

    /**
     * 开发者后台->应用开发-企业内部应用->选择您的机器人->应用凭证->AppKey
     */
    private static String key;

    /**
     * 开发者后台->应用开发-企业内部应用->选择您的机器人->应用凭证->AppSecret
     */
    private static String secret;

    /**
     * 群聊->机器人头像->机器人设置（有权限，不一定都能看到）->Webhook
     */
    private static String webhook;

    @Value("${ding.app-key}")
    public void setKey(String key) {
        AccessTokenUtil.key = key;
    }
    public static String getKey() { return AccessTokenUtil.key; }

    @Value("${ding.app-secret}")
    public void setSecret(String secret) {
        AccessTokenUtil.secret = secret;
    }
    public static String getSecret() { return AccessTokenUtil.secret; }

    @Value("${ding.webhook}")
    public void setWebhook(String webhook) {
        AccessTokenUtil.webhook = webhook;
    }
    public static String getWebhook() { return AccessTokenUtil.webhook; }

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public AccessTokenUtil(StringRedisTemplate stringRedisTemplate) {
        AccessTokenUtil.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String DING_ACCESS_TOKEN = "ding_access_token_";

    /**
     * 检查签名是否正确
     * @param timestamp 时间戳
     * @param sign 签名
     * @return 是否正确
     */
    public static Boolean checkSign(Long timestamp, String sign){
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return Objects.equals(new String(Base64.encodeBase64(signData)), sign);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;
    }

    /**
     * 获取企业内部应用的access_token
     * 在使用access_token时，请注意：
     * access_token的有效期为7200秒（2小时），有效期内重复获取会返回相同结果并自动续期，过期后获取会返回新的access_token。
     * 开发者需要缓存access_token，用于后续接口的调用。因为每个应用的access_token是彼此独立的，所以进行缓存时需要区分应用来进行存储。
     * 不能频繁调用gettoken接口，否则会受到频率拦截。
     * @return access_token
     */
    public static String getAccessToken(){
        try {
            String accessToken = DingTalkUtil.getKeyObjectValue(DING_ACCESS_TOKEN + key, String.class);
            if(StringUtils.isBlank(accessToken)){
                DefaultDingTalkClient client = new DefaultDingTalkClient(UrlConstant.GET_ACCESS_TOKEN_URL);
                OapiGettokenRequest request = new OapiGettokenRequest();
                request.setAppkey(key);
                request.setAppsecret(secret);
                request.setHttpMethod("GET");
                OapiGettokenResponse response = client.execute(request);
                accessToken = response.getAccessToken();
                DingTalkUtil.delete(DING_ACCESS_TOKEN + key);
                DingTalkUtil.setStringKeyAndValue(DING_ACCESS_TOKEN + key, accessToken, 100);
                log.info("缓存accessToken成功，accessToken：{}", accessToken);
            }
            return accessToken;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取webhook和签名sign
     * @return webhook和签名sign
     */
    public static String getWebhookAndSign() {
        return webhook + getSign();
    }

    /**
     * 获取签名sign
     * @return sign
     */
    public static String getSign() {
        try {
            Long timestamp = System.currentTimeMillis();
            StringBuilder stringBuilder = new StringBuilder("&timestamp=");
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            return stringBuilder.append(timestamp).append("&sign=").append(sign).toString();
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return "";
    }

    /**
     * 初始化 Client
     * @param cls 各类Client对象
     * @param <T> extends com.aliyun.teaopenapi.Client
     * @return Client
     * @throws Exception
     */
    public static <T extends com.aliyun.teaopenapi.Client> T createClient(Class<T> cls) throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        Constructor<?> constructor = cls.getConstructor(Config.class);
        return (T) constructor.newInstance(config);
    }

    /**
     * 通过手机号获取用户信息
     * 注意：员工离职后，无法再通过手机号获取userId。
     * @param phone
     * @return 用户id
     */
    public static String getUserIdByMobile(String phone) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(UrlConstant.GET_USER_INFO_BY_MOBILE_URL);
            OapiV2UserGetbymobileRequest req = new OapiV2UserGetbymobileRequest();
            req.setMobile(phone);
            req.setSupportExclusiveAccountSearch(true);
            OapiV2UserGetbymobileResponse rsp = client.execute(req, getAccessToken());
            return null != rsp.getResult() ? rsp.getResult().getUserid() : null;
        } catch (ApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
