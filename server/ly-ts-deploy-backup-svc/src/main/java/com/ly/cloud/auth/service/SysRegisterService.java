package com.ly.cloud.auth.service;


import com.github.benmanes.caffeine.cache.Cache;
import com.ly.cloud.auth.commonException.KaptchaException;
import com.ly.cloud.auth.constant.UserConstants;
import com.ly.cloud.auth.po.SysUser;
import com.ly.cloud.auth.dto.RegisterBody;
import com.ly.cloud.auth.util.StringUtils;
import com.ly.cloud.backup.common.constant.RedisConstants;
import com.ly.cloud.backup.util.Sm4Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 注册校验方法
 * 
 *
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private Cache cache;

    @Value("${auth.captchaOnOff:true}")
    private boolean captchaOnOff;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();

        // 验证码开关
        if (captchaOnOff)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getCaptchaUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username)))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            try {
                sysUser.setPassword(Sm4Util.sm4EcbEncrypt(registerBody.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
                return "注册失败，密码加密错误";
            }
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = RedisConstants.REDIS_KEY_PREFIX_TC_VERIFICATION_CODE_MODULE + StringUtils.nvl(uuid, "");
        String captcha = (String) cache.getIfPresent(verifyKey);
        cache.asMap().remove(verifyKey);
        if (captcha == null)
        {
            throw new KaptchaException(51006,"验证码已过期,请重新获取！");
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new KaptchaException(51007,"验证码输入不正确，请重新输入！");
        }
    }
}
