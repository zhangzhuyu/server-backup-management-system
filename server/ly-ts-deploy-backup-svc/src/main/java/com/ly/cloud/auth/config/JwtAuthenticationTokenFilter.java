package com.ly.cloud.auth.config;


import com.alibaba.fastjson.JSON;
import com.ly.cloud.auth.common.Result;
import com.ly.cloud.auth.constant.Constants;
import com.ly.cloud.auth.dto.LoginUser;
import com.ly.cloud.auth.service.TokenService;
import com.ly.cloud.auth.util.SecurityUtils;
import com.ly.cloud.auth.util.ServletUtils;
import com.ly.cloud.auth.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 *
 *
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    // 令牌自定义标识
    @Value("${auth.token.header}")
    private String header;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 获取请求携带的令牌
        String token = getToken(request);
        if(StringUtils.isNotEmpty(token)) {
            LoginUser loginUser = tokenService.getLoginUser(request);
            if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
                tokenService.verifyToken(loginUser);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //2023.11.8 增加登陆用户信息供controller使用
                request.setAttribute("user",loginUser);
            }else {
                response.setStatus(302);
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.getWriter().print(JSON.toJSONString(Result.error(302, "会话已过期，请重新登录！")));
                return;
            }
        }
        chain.doFilter(request, response);
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

}
