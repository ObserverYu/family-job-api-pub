package org.chen.framework.advice;

import com.alibaba.fastjson.JSONObject;
import org.chen.framework.annotion.IgnoreResponseAdviceFlag;
import org.chen.framework.annotion.SaveRequestTimeFlag;
import org.chen.framework.result.FastResponseUtil;
import org.chen.framework.result.ResultModel;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

/**
 * @author ：YuChen
 * @date ：Created in 2019-02-26 21:17
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {

        if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdviceFlag.class)){
            return false;
        }
        Method method = methodParameter.getMethod();
        if(method != null){
            if (methodParameter.getMethod().isAnnotationPresent(IgnoreResponseAdviceFlag.class)
                    || methodParameter.getMethod().getReturnType().isAssignableFrom(ResultModel.class)
                    || methodParameter.getMethod().getReturnType().isAssignableFrom(JSONObject.class)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter,
								  MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
								  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 跳过swagger
        if (serverHttpRequest.getURI().toString().contains("swagger")||serverHttpRequest.getURI().toString().contains("api-doc")){
            return o;
        }
        // 处理spring原生的错误
        if(o instanceof LinkedHashMap){
            LinkedHashMap map = (LinkedHashMap) o;
            Object status = map.get("status");
            if(status instanceof Integer){
                try{
                    if((Integer)status >= 300){
                        ResultModel res = new ResultModel();
                        res.setCode((Integer)status);
                        res.setMessage((String)map.get("error") + ","+(String)map.get("message") );
                        return res;
                    }
                }catch (Exception e){

                }
            }
        }
        // 计算请求消耗的时间
        Method method = methodParameter.getMethod();
        if(method != null){
            if (methodParameter.getMethod().isAnnotationPresent(SaveRequestTimeFlag.class) ){
                ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
                if(attributes != null){
                    HttpServletRequest request = attributes.getRequest();
                    Object startTimeObj = request.getAttribute("startTime");
                    if(startTimeObj != null){
                        Long startTime = (Long) startTimeObj;
                        Long endTime = System.currentTimeMillis();
                        Long runTime= (endTime - startTime);
                        return FastResponseUtil.getSuccessResult(o,runTime,endTime);
                    }
                }

            }
        }
        return FastResponseUtil.getSuccessResult(o);

    }
}
