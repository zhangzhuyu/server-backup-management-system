package com.ly.cloud.auth.service;


import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.ly.cloud.auth.constant.Constants;
import com.ly.cloud.auth.dto.LoginUser;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.auth.util.uuid.IdUtils;
import com.ly.cloud.backup.config.RedisConf;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.ly.cloud.backup.common.constant.RedisConstants.REDIS_KEY_PREFIX_TC_LOGIN_MODULE;

/**
 * token验证处理
 *
 *
 */
@Component
public class TokenService
{
    // 令牌自定义标识
    @Value("${auth.token.header}")
    private String header;

    // 令牌秘钥
    @Value("${auth.token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${auth.token.expireTime:30}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 30 * 60 * 1000L;

    @Autowired
    private Cache cache;

    @Autowired
    private RedisConf redisUtil;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
//                LoginUser user = (LoginUser) cache.getIfPresent(userKey);
                //todo 从redis中获取token
                String redisKey = REDIS_KEY_PREFIX_TC_LOGIN_MODULE+userKey;
                String mapString = String.valueOf(redisUtil.get(redisKey));
                LoginUser loginUser = JSONObject.parseObject(mapString, LoginUser.class);
                if (StringUtils.isNotEmpty(mapString) &&loginUser != null) {
                    return loginUser;
                }
//                return user;
            }catch (Exception e){

            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            cache.asMap().remove((userKey));
            // TODO: 2022/7/20  从redis删除token
            String redisKey =  REDIS_KEY_PREFIX_TC_LOGIN_MODULE+userKey;
            redisUtil.del(redisKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser)
    {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if ((expireTime - currentTime) <= MILLIS_MINUTE_TEN)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        //本地缓存的话，启用多实例会出错
//        cache.put(userKey, loginUser);
        // TODO: 2022/7/20  将token缓存到redis
        String redisKey = REDIS_KEY_PREFIX_TC_LOGIN_MODULE+userKey;
        redisUtil.set(redisKey, JSONObject.toJSONString(loginUser),expireTime);
    }


    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid)
    {
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }
}
