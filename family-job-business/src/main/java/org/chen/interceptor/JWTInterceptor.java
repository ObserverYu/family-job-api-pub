package org.chen.interceptor;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.chen.annotion.Auth;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.businessex.BusinessExceptionEnum;
import org.chen.property.RedisPrefixProperties;
import org.chen.util.token.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * JWT验证拦截器
 *
 * @author YuChen
 * @date 2018/9/12
 **/
@Slf4j
@Component
public class JWTInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private RedisPrefixProperties redisPrefixProperties;

    @Value("${spring.profiles.active}")
    private String profile;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof ResourceHttpRequestHandler) {
            log.debug("静态资源，放行,uri:{},url:{}"
                    ,request.getRequestURI()
                    ,request.getRequestURL().toString());
            return true;
        }
        String token = request.getHeader(redisPrefixProperties.getTokenHeader());
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Auth auth = method.getAnnotation(Auth.class);
        if (auth != null) {
            // 获取header中的JWT参数
            if (StrUtil.isEmpty(token)) {
                throw new BusinessException(BusinessExceptionEnum.TOKEN_EMPTY);
            }
            // token不为空，表示用户已经登录，需要校验
            Long userId;
            try{
                userId = tokenUtil.checkToken(token);
            }catch (BusinessException e){
                if("dev".equals(profile) && e.getCode().equals(401)){
                    userId = Long.valueOf(token);
                }else {
                    throw e;
                }
            }
            request.setAttribute("userId", userId);
        }
        // 如果没有auth注解表示不需要校验JWT，直接返回true
        return true;
    }
}

