package org.chen.framework.interceptor;


import org.chen.framework.annotion.LoggingFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.util.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 清除threadlocal中储存的参数数据
 *
 * @author YuChen
 * @date 2020-5-28 14:01:10
 **/
@Slf4j
@Component
public class LoggingRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            SaveRequestTimeFlag methodAnnotation = handlerMethod.getMethodAnnotation(SaveRequestTimeFlag.class);
            if (methodAnnotation != null) {
                // 保存接口开始处理的时间  粗略计算接口调用消耗的时间
                request.setAttribute("startTime", System.currentTimeMillis());
            }
        }
        return true;
    }

    /**
     * Callback after completion of request processing, that is, after rendering
     * the view. Will be called on any outcome of handler execution, thus allows
     * for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's {@code preHandle}
     * method has successfully completed and returned {@code true}!
     * <p>As with the {@code postHandle} method, the method will be invoked on each
     * interceptor in the chain in reverse order, so the first interceptor will be
     * the last to be invoked.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation is empty.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  handler (or {@link HandlerMethod}) that started asynchronous
     *                 execution, for type and/or instance examination
     * @param ex       any exception thrown on handler execution, if any; this does not
     *                 include exceptions that have been handled through an exception resolver
     * @throws Exception in case of errors
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoggingFlag methodAnnotation = handlerMethod.getMethodAnnotation(LoggingFlag.class);
            if (methodAnnotation != null) {
                if(methodAnnotation.loggingUri()){
                    log.info("请求uri:{}",request.getRequestURI());
                }
                if(methodAnnotation.loggingHeader()){
                    Enumeration<String> headerNames = request.getHeaderNames();
                    Map<String,String> headers = new HashMap<>();
                    while(headerNames.hasMoreElements()){
                        String key = headerNames.nextElement();
                        headers.put(key,request.getHeader(key));
                    }
                    log.info("请求头:{}",headers);
                }
                if(methodAnnotation.logging()){
                    log.info("获取到的入参实体:{}", RequestHolder.getObj());
                }
            }
        }
    }

}

