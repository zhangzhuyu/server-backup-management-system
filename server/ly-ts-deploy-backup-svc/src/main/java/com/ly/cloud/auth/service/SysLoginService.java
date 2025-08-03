package com.ly.cloud.auth.service;

import com.ly.cloud.auth.commonException.KaptchaException;

import com.ly.cloud.auth.dto.LoginUser;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.constant.RedisConstants;
import com.ly.cloud.backup.config.RedisConf;
import com.ly.cloud.backup.exception.ServiceException;
import com.ly.cloud.backup.exception.user.UserPasswordNotMatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisConf redisUtil;

    @Value("${auth.captchaOnOff:true}")
    private boolean captchaOnOff;

    /**
     * 登录验证
     * need
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) throws UserPasswordNotMatchException, ServiceException {
        // 验证码开关
        if (captchaOnOff && StringUtils.isNotEmpty(uuid)) {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                throw new UserPasswordNotMatchException();
            } else {
                throw new ServiceException(e.getMessage());
            }
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {
        String verifyKey = RedisConstants.REDIS_KEY_PREFIX_TC_VERIFICATION_CODE_MODULE + StringUtils.nvl(uuid, "");
        String captcha = (String) redisUtil.get(verifyKey);
        if (captcha == null) {
            throw new KaptchaException(51006, "验证码已过期,请重新获取！");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new KaptchaException(51007, "验证码输入不正确，请重新输入！");
        }
//        // 如果验证通过则直接删除验证码
//        redisUtil.del(verifyKey);
    }

}
