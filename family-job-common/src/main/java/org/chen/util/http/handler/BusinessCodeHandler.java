package org.chen.util.http.handler;


import org.chen.util.http.OkHttpResponseUtils;

/**
* 处理业务的特殊返回code处理器
*
* @author YuChen
* @date 2020/7/15 15:10
*/
public interface BusinessCodeHandler<R,E> {

    void handle(OkHttpResponseUtils.BaseErrorHandleResult baseErrorHandleResult, OkHttpResponseUtils.JsonResult<R, E> allResult, Object param, String apiName);

    String getCode(OkHttpResponseUtils.JsonResult<R, E> allResult);

    String getMsg(OkHttpResponseUtils.JsonResult<R, E> allResult);

    Boolean checkCode(String code);


}

