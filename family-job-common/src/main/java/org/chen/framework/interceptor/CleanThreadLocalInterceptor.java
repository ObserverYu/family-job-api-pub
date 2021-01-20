package org.chen.framework.interceptor;


import org.chen.framework.annotion.LoggingFlag;
import org.chen.util.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 清除threadlocal中储存的参数数据
 *
 * @author YuChen
 * @date 2020-5-28 14:01:10
 **/
@Slf4j
@Component
public class CleanThreadLocalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoggingFlag methodAnnotation = handlerMethod.getMethodAnnotation(LoggingFlag.class);
            if (methodAnnotation != null) {
                RequestHolder.remove();
            }
        }
    }

}

