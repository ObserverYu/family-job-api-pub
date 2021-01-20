package org.chen.framework.exhandler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.chen.framework.businessex.BusinessException;
import org.chen.framework.result.ResultModel;
import org.chen.util.RequestHolder;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author YuChen
 * @date 2020-4-17
 **/
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResultModel<String> createExceptionResult(String msg, Integer code,HttpServletRequest request) {
        ResultModel<String> res = new ResultModel<>();
        res.setMessage(msg == null ? "系统错误" : msg);
        res.setCode(code == null ? 500 : code);
        res.setData("");
        Object startTimeObj = null;
        if(request != null){
            startTimeObj = request.getAttribute("startTime");
        }
        if(startTimeObj != null){
            Long startTime = (Long) startTimeObj;
            Long endTime = System.currentTimeMillis();
            Long runTime= (endTime - startTime);
            res.setRunTime(runTime);
            res.setTimestamp(endTime);
        }
        return res;
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class,BindException.class})
    public ResultModel<String> methodArgumentNotValidExceptionHandler(Exception ex,HttpServletRequest request) {
        FieldError fieldError = null;
        if(ex instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException subEx = (MethodArgumentNotValidException)ex;
            fieldError = subEx.getBindingResult().getFieldError();
        }else if(ex instanceof BindException){
            BindException subEx = (BindException)ex;
            fieldError = subEx.getBindingResult().getFieldError();
        }
        Integer code = 403;
        String msg = null;
        if (fieldError != null) {
            msg = "["+fieldError.getField() + "]" + fieldError.getDefaultMessage();
        }
        return createExceptionResult(msg, code,request);
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResultModel<String> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex,HttpServletRequest request) {
        Integer code = 400;
        String msg = "传参方式错误:"+ex.getMessage();
        return createExceptionResult(msg, code,request);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResultModel<String> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex,HttpServletRequest request) {
        Integer code = 400;
        String msg = "请求方式错误:"+ex.getMessage();
        return createExceptionResult(msg, code,request);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResultModel<String> businessExceptionHandler(BusinessException ex,HttpServletRequest request) {
        Integer code = ex.getCode();
        String msg = ex.getMessage();
        return createExceptionResult(msg, code,request);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResultModel<String> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,HttpServletRequest request) {
        Integer code = 400;
        String msg = null;
        if (StrUtil.isNotBlank(ex.getMessage()) && ex.getMessage().startsWith("Required request body is missing")) {
            msg = "缺少请求体";
        }else {
            log.warn("发生未捕获的HttpMessageNotReadableException被拦截", ex);
            msg = ex.getMessage();
        }
        return createExceptionResult(msg, code,request);
    }

//    @ExceptionHandler(value = BlockException.class)
//    public ResultModel<String> blockExceptionHandler(BlockException ex,HttpServletRequest request) {
//        Integer code = 501;
//        String resource = "["+ex.getRule().getResource()+"]";
//        StringBuilder msgB = new StringBuilder();
//        msgB.append("资源").append(resource);
//        if(ex instanceof FlowException){
//            log.warn("资源"+resource+"触发限流规则,rule:{}",ex.getRule());
//            msgB.append("请求过多被限流");
//        }else if(ex instanceof DegradeException){
//            log.warn("资源"+resource+"触发熔断规则,rule:{}",ex.getRule());
//            msgB.append("不稳定被熔断降级");
//        }else {
//            log.warn("资源"+resource+"被限制,rule:{}",ex.getRule());
//            msgB.append("被限制");
//        }
//        return createExceptionResult(msgB.toString(), code,request);
//    }

    @ExceptionHandler(value = Exception.class)
    public ResultModel<String> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String msg = "系统错误";
        Integer code = 500;
        log.warn("发生未捕获的异常被拦截", ex);
        String uri = null;
        String method = null;
        String queryString = null;
        if (request != null) {
            uri = request.getRequestURI();
            method = request.getMethod();
            queryString = request.getQueryString();
        }
        Object obj = RequestHolder.getObj();
        log.warn("发生未捕获的异常被拦截,request基本信息 uri:{},method:{},queryString:{},bodyParam:{}"
                , uri == null ? "" : uri
                , method == null ? "" : method
                , queryString == null ? "" : queryString
                , obj == null ? "" : obj
        );
        //RequestHolder.remove();
        return createExceptionResult(msg,code,request);
    }
}
