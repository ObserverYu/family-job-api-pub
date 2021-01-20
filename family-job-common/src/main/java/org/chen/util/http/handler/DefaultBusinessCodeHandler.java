package org.chen.util.http.handler;


import org.chen.util.http.OkHttpResponseUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/7/16 10:11
 **/

@Slf4j
public class DefaultBusinessCodeHandler<R,E> implements BusinessCodeHandler<R,E> {

    @Override
    public void handle(OkHttpResponseUtils.BaseErrorHandleResult baseErrorHandleResult, OkHttpResponseUtils.JsonResult<R, E> allResult, Object param, String apiName) {
        String code = getCode(allResult);
        if(code == null){
            log.error("["+apiName+"]"+"原接口返回结果没有code,res:{},param:{}",allResult,param);
            log.error("原始返回:{}",allResult.getBodyStr());
            baseErrorHandleResult.setSimpleMsg("["+apiName+"]"+"原接口返回异常");
            baseErrorHandleResult.setType(OkHttpResponseUtils.BaseErrorHandleResult.TYPE_ERROR);
            return;
        }
        Boolean successCode = checkCode(code);
        if(!successCode){
            String msg = getMsg(allResult);
            log.error("["+apiName+"]"+"原接口返回错误码,res:{},param:{}",allResult,param);
            log.error("原始返回:{}",allResult.getBodyStr());
            baseErrorHandleResult.setSimpleMsg("["+apiName+"]"+"原接口返回错误码,msg:"+msg+",code:"+code);
            baseErrorHandleResult.setType(OkHttpResponseUtils.BaseErrorHandleResult.TYPE_OTHER);
        }
    }

    @Override
    public String getCode(OkHttpResponseUtils.JsonResult<R, E> allResult) {
        return null;
    }

    @Override
    public String getMsg(OkHttpResponseUtils.JsonResult<R, E> allResult) {
        return null;
    }

    @Override
    public Boolean checkCode(String code) {
        return null;
    }
}
