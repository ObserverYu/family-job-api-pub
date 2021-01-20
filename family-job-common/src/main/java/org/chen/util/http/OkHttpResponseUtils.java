package org.chen.util.http;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.chen.util.http.handler.BusinessCodeHandler;
import org.chen.util.http.handler.ResponseFailedHandler;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 根据okhttp request获取response
 *
 * @author YuChen
 */
@Slf4j
public class OkHttpResponseUtils {

    /**
     * 获取json返回 处理https传输层面的问题
     *
     * @param request 请求 okhttp
     * @param clazz body中反序列化出来的实体类
     * @param handler 对某些其他情况的response的处理 特别是某些NT的接口正常返回json数据异常居然返回html代码 很恶心 说的就是证照库
     * @param isList 某些NT接口虽然返回json 但是没有统一包装结构 直接返回业务数据 有时候业务数据还是一个LIST 说的也是证照库
     * @return
     * @author YuChen
     * @date 2020/4/17 14:41
     */
    public static <R, E> JsonResult<R, E> getResultFromJsonBody(Request request, Class<R> clazz, ResponseFailedHandler<E> handler, boolean isList) {
        JsonResult<R, E> res = new JsonResult<>();
        if (request == null || clazz == null) {
            throw new IllegalArgumentException("request和class不能为空");
        }
        OkHttpClient httpClient = OkhttpUtils.getHttpClient();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (Exception e) {
            log.error("调用接口出现异常", e);
            log.error("调用接口出现异常,url:{}, request:{}", request.url().toString(), request.toString());
            return res;
        }
        if (response == null || !response.isSuccessful()) {
            log.warn("接口返回错误,url:{}, request:{}, response:{}"
                    , request.url()
                    , request.toString()
                    , response == null ? "nul" : response.toString()
            );
            if(handler != null){
                E handleRes = handler.handle(null, response);
                res.setHandleRes(handleRes);
            }
            return res;
        }
        Headers headers = response.headers();
        res.setHeaderStr(headers.toString());
        res.setResponseCode(response.code());
        res.setResponseMessage(response.message());
        String bodyStr;
        try {
            bodyStr = response.body().string();
        } catch (Exception e) {
            log.error("获取responseBody出现异常,response:{}", response);
            log.error("获取responseBody出现异常, request:{}, response:{}"
                    , request.toString()
                    , response.toString()
            );
            return res;
        }
        if (StrUtil.isBlank(bodyStr)) {
            log.warn("responseBody出为空,request:{}, response:{}"
                    , request.toString()
                    , response.toString()
            );
            return res;
        }
        res.setBodyStr(bodyStr);
        try {
            if (isList) {
                List<R> jsonResList = JSONObject.parseArray(bodyStr, clazz);
                res.setJsonResList(jsonResList);
            } else {
                R jsonRes = JSONObject.parseObject(bodyStr, clazz);
                res.setJsonRes(jsonRes);
            }
        } catch (Exception e) {
            log.error("json序列化失败,str:{}", bodyStr);
            if (handler != null) {
                E handleRes = handler.handle(bodyStr, response);
                res.setHandleRes(handleRes);
            }
            return res;
        }
        res.setSuccess(true);
        return res;
    }

    /**
     * 获取json返回 处理https传输层面的问题
     *
     * @param request 请求 okhttp
     * @param clazz body中反序列化出来的实体类
     * @param handler 对某些其他情况的response的处理 特别是某些NT的接口正常返回json数据异常居然返回html代码 很恶心 说的就是证照库
     * @param isList 某些NT接口虽然返回json 但是没有统一包装结构 直接返回业务数据 有时候业务数据还是一个LIST 说的也是证照库
     * @param saveTime 是否记录接口响应时间
     * @return
     * @author YuChen
     * @date 2020/4/17 14:41
     */
    public static <R, E> JsonResult<R, E> getResultFromJsonBody(Request request, Class<R> clazz
            , ResponseFailedHandler<E> handler, boolean isList, boolean saveTime, String apiName) {
        JsonResult<R, E> res = new JsonResult<>();
        if (request == null || clazz == null) {
            throw new IllegalArgumentException("request和class不能为空");
        }
        OkHttpClient httpClient = OkhttpUtils.getHttpClient();
        Response response = null;
        long startTime = 0;
        long endTime = 0;
        long requestTime  = 0;
        String name = StrUtil.isBlank(apiName) ? request.url().toString() : apiName;
        try {
            if(saveTime){
                startTime = System.currentTimeMillis();
            }
            response = httpClient.newCall(request).execute();
        }catch (SocketTimeoutException e){
            log.error("调用接口[{}]超时,url:{}, request:{}",name, request.url().toString(), request.toString());
        }catch (Exception e) {
            log.error("调用接口出现异常", e);
            log.error("调用接口[{}]出现异常,url:{}, request:{}",name, request.url().toString(), request.toString());
            return res;
        }finally {
            if(saveTime){
                endTime = System.currentTimeMillis();
                requestTime = endTime - startTime;
                res.setRequestTime(requestTime);
                log.info("接口[{}]调用耗时:{}",name,requestTime);
            }
        }
        if (response == null || !response.isSuccessful()) {
            log.error("接口[{}]返回错误,url:{}, request:{}, response:{}"
                    ,name
                    , request.url()
                    , request.toString()
                    , response == null ? "nul" : response.toString()
            );
            if(handler != null){
                E handleRes = handler.handle(null, response);
                res.setHandleRes(handleRes);
            }
            return res;
        }
        Headers headers = response.headers();
        res.setHeaderStr(headers.toString());
        res.setResponseCode(response.code());
        res.setResponseMessage(response.message());
        String bodyStr;
        try {
            bodyStr = response.body().string();
        } catch (Exception e) {
            log.error("获取responseBody出现异常,response:{}", response);
            log.error("接口[{}]获取responseBody出现异常, request:{}, response:{}"
                    ,name
                    , request.toString()
                    , response.toString()
            );
            return res;
        }
        if (StrUtil.isBlank(bodyStr)) {
            log.warn("接口[{}]responseBody出为空, request:{}, response:{}"
                    ,name
                    , request.toString()
                    , response.toString()
            );
            return res;
        }
        res.setBodyStr(bodyStr);
        try {
            if (isList) {
                List<R> jsonResList = JSONObject.parseArray(bodyStr, clazz);
                res.setJsonResList(jsonResList);
            } else {
                R jsonRes = JSONObject.parseObject(bodyStr, clazz);
                res.setJsonRes(jsonRes);
            }
        } catch (Exception e) {
            log.error("json序列化失败,str:{}", bodyStr);
            if (handler != null) {
                E handleRes = handler.handle(bodyStr, response);
                res.setHandleRes(handleRes);
            }
            return res;
        }
        res.setSuccess(true);
        return res;
    }

    /**
     * 异步获取json返回 处理https传输层面的问题
     *
     * @param res      结果获取器,需要传入
     * @param request  请求 okhttp
     * @param clazz    body中反序列化出来的实体类
     * @param handler  对某些其他情况的response的处理 特别是某些NT的接口正常返回json数据异常居然返回html代码 很恶心 说的就是证照库
     * @param isList   某些NT接口虽然返回json 但是没有统一包装结构 直接返回业务数据 有时候业务数据还是一个LIST 说的也是证照库
     * @param saveTime 是否记录接口响应时间
     * @return
     * @author YuChen
     * @date 2020/4/17 14:41
     */
    public static <R, E> void getResultFromJsonBodyEnqueue(JsonResult<R, E> res, Request request, Class<R> clazz
            , ResponseFailedHandler<E> handler, boolean isList, boolean saveTime, String apiName, CountDownLatch countDownLatch) {
        if (request == null || clazz == null) {
            throw new IllegalArgumentException("request和class不能为空");
        }
        OkHttpClient httpClient = OkhttpUtils.getHttpClient();
        final String name = StrUtil.isBlank(apiName) ? request.url().toString() : apiName;
        Call call = httpClient.newCall(request);
        final long startTime = System.currentTimeMillis();
        AsyncCallBack<R, E> reAsyncCallBack
                = new AsyncCallBack<>(countDownLatch, saveTime, startTime, res, apiName, request, handler, isList, clazz);
        log.debug("["+apiName+"]"+"请求入队");
        call.enqueue(reAsyncCallBack);
    }


    /**
    * 对请求结果的基本处理, 主要是请求失败和返回的结果有误的情况  业务层面
    *
    * @param allResult 经过对response处理后获得到的基本json数据
    * @param handler 不同的接口一般有不同的统一包装结构  该处理器对统一包装结构进行处理  过滤出成功(一般根据code判断)的结果  对失败的结果进行定制化处理
    * @param param 接口参数 日志记录用
    * @param apiName 接口名字 日志记录和返回消息用
    * @return
    * @author YuChen
    * @date 2020/7/15 15:08
    */
    public static <R, E, P> BaseErrorHandleResult baseErrorHandle(JsonResult<R, E> allResult, BusinessCodeHandler<R,E> handler, P param, String apiName){
        BaseErrorHandleResult res = new BaseErrorHandleResult();
        // 初始化为成功
        res.setType(BaseErrorHandleResult.TYPE_SUCCESS);
        boolean success = allResult.getSuccess();
        R jsonResult = allResult.getJsonRes();
        String simpleMsg = "";
        if(!success){
            simpleMsg = "[" + apiName +"]" + "原接口调用失败";
            res.setSimpleMsg(simpleMsg);
            res.setType(BaseErrorHandleResult.TYPE_FAIL);
            log.error(simpleMsg + ",res:{},param:{}",allResult,param);
            log.error("原始返回:{}",allResult.getBodyStr());
            return res;
        }else if (jsonResult == null){
            simpleMsg = "[" + apiName +"]" + "原接口返回值异常";
            res.setType(BaseErrorHandleResult.TYPE_ERROR);
            res.setSimpleMsg(simpleMsg);
            log.error(simpleMsg + ",res:{},param:{}",allResult,param);
            log.error("原始返回:{}",allResult.getBodyStr());
            return res;
        }
        handler.handle(res,allResult,param,apiName);
        return res;
    }

    /**
    * 校验请求是否成功,并将结果传回
    *
    * @param
    * @return
    * @author YuChen
    * @date 2020/7/15 15:05
    */
    @ToString
    public static class BaseErrorHandleResult {
        public static final Integer TYPE_SUCCESS = 0;
        public static final Integer TYPE_FAIL = 1;
        public static final Integer TYPE_ERROR = 2;
        public static final Integer TYPE_OTHER = 3;
        // 错误类型  0-成功 1-原接口调用失败  2-原接口返回值异常 3-其他
        private Integer type;
        // 原始错误代码
        private String errorCode;
        // 原始错误消息
        private String errorMsg;
        // 自定义简单消息
        private String simpleMsg;
        // 原始完整错误
        private JSONObject originalData;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getSimpleMsg() {
            return simpleMsg;
        }

        public void setSimpleMsg(String simpleMsg) {
            this.simpleMsg = simpleMsg;
        }

        public JSONObject getOriginalData() {
            return originalData;
        }

        public void setOriginalData(JSONObject originalData) {
            this.originalData = originalData;
        }
    }


    /**
     * 请求直接转json的结果获取器
     *
     * @author YuChen
     * @date 2020/4/17 14:59
     */
    @ToString
    public static class JsonResult<R, E> {
        // json结果
        private R jsonRes;

        // json结果
        private List<R> jsonResList;

        // 序列化失败后的处理结果
        private E handleRes;

        // 是否成功
        private boolean success;

        // 使用了多长时间  毫秒
        private long requestTime;

        // 原始body
        private String bodyStr;

        // 原始header
        private String headerStr;

        // 原始code
        private int responseCode;

        // 原始message
        private String responseMessage;

        public int getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }

        public String getHeaderStr() {
            return headerStr;
        }

        public void setHeaderStr(String headerStr) {
            this.headerStr = headerStr;
        }

        public String getBodyStr() {
            return bodyStr;
        }

        public void setBodyStr(String bodyStr) {
            this.bodyStr = bodyStr;
        }

        public long getRequestTime() {
            return requestTime;
        }

        public void setRequestTime(long requestTime) {
            this.requestTime = requestTime;
        }

        public R getJsonRes() {
            return jsonRes;
        }

        public void setJsonRes(R jsonRes) {
            this.jsonRes = jsonRes;
        }

        public E getHandleRes() {
            return handleRes;
        }

        public void setHandleRes(E handleRes) {
            this.handleRes = handleRes;
        }

        public boolean getSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public List<R> getJsonResList() {
            return jsonResList;
        }

        public void setJsonResList(List<R> jsonResList) {
            this.jsonResList = jsonResList;
        }
    }

    /**
     * 异步请求回调类 匿名内部类内的变量是外部局部变量的复制, CountDownLatch是无法使用同一个的 所以必须建一个类将变量传入
     *
     * @param
     * @author YuChen
     * @return
     * @date 2020/8/5 11:37
     */
    public static class AsyncCallBack<R, E> implements Callback {

        private CountDownLatch countDownLatch;

        private boolean saveTime;

        private long startTime;

        private JsonResult<R, E> res;

        private String apiName;

        private Request request;

        private ResponseFailedHandler<E> handler;

        private boolean isList;

        private Class<R> clazz;


        /**
         * 构造
         *
         * @param countDownLatch 控制线程配合
         * @param saveTime 是否计算响应时间
         * @param startTime 开始时间
         * @param res 结果收集器
         * @param apiName api名,打印日志用
         * @param request 原始请求
         * @param handler 失败处理器
         * @param isList 结果是否是list
         * @param clazz 序列化的类
         * @param clazz 序列化的类
         * @return
         * @author YuChen
         * @date 2020/8/5 16:08
         */
        public AsyncCallBack(CountDownLatch countDownLatch, boolean saveTime, long startTime, JsonResult<R, E> res
                , String apiName, Request request, ResponseFailedHandler<E> handler, boolean isList, Class<R> clazz) {
            this.countDownLatch = countDownLatch;
            this.saveTime = saveTime;
            this.startTime = startTime;
            this.res = res;
            this.apiName = apiName;
            this.request = request;
            this.handler = handler;
            this.isList = isList;
            this.clazz = clazz;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if (saveTime) {
                long endTime = System.currentTimeMillis();
                long requestTime = endTime - startTime;
                res.setRequestTime(requestTime);
                log.info("接口[{}]异步调用耗时:{}", apiName, requestTime);
            }
            log.error("异步调用接口" + "[" + apiName + "]" + "出现异常", e);
            log.error("异步调用接口[{}]出现异常,url:{}, request:{}", apiName, request.url().toString(), request.toString());
            failCountDown();
        }

        @Override
        public void onResponse(Call call, Response response){
            if (saveTime) {
                long endTime = System.currentTimeMillis();
                long requestTime = endTime - startTime;
                res.setRequestTime(requestTime);
                log.info("接口[{}]异步调用耗时:{}", apiName, requestTime);
            }
            if (!response.isSuccessful()) {
                log.error("接口[{}]返回错误,url:{}, request:{}, response:{}"
                        , apiName
                        , request.url()
                        , request.toString()
                        , response.toString()
                );
                if (handler != null) {
                    E handleRes = handler.handle(null, response);
                    res.setHandleRes(handleRes);
                }
                successCountDown(res);
                return;
            }
            Headers headers = response.headers();
            res.setHeaderStr(headers.toString());
            res.setResponseCode(response.code());
            res.setResponseMessage(response.message());
            if (handler != null) {
                E handleRes = handler.handle(null, response);
                res.setHandleRes(handleRes);
            }
            String bodyStr = null;
            try {
                bodyStr = response.body().string();
            } catch (Exception e) {
                log.error("获取responseBody出现异常,response:{}", response);
                log.error("接口[{}]获取responseBody出现异常, request:{}, response:{}"
                        , apiName
                        , request.toString()
                        , response.toString()
                );
                successCountDown(res);
                return;
            }
            if (StrUtil.isBlank(bodyStr)) {
                log.warn("接口[{}]responseBody出为空, request:{}, response:{}"
                        , apiName
                        , request.toString()
                        , response.toString()
                );
                successCountDown(res);
                return;
            }
            res.setBodyStr(bodyStr);
            try {
                if (isList) {
                    List<R> jsonResList = JSONObject.parseArray(bodyStr, clazz);
                    res.setJsonResList(jsonResList);
                } else {
                    R jsonRes = JSONObject.parseObject(bodyStr, clazz);
                    res.setJsonRes(jsonRes);
                }
            } catch (Exception e) {
                log.error("json序列化失败,str:{}", bodyStr);
                if (handler != null) {
                    E handleRes = handler.handle(bodyStr, response);
                    res.setHandleRes(handleRes);
                }
            }
            res.setSuccess(true);
            successCountDown(res);
        }

        private void successCountDown(JsonResult<R, E> res){
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }

        private void failCountDown(){
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

}