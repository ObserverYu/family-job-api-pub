package org.chen.framework.result;

import com.alibaba.fastjson.JSONObject;

/**
 * 工具类
 *
 * @author YuChen
 * @date 2018/12/6
 **/
public class FastResponseUtil {

    /**
     * 根据data获取成功返回信息
     *
     * @param data    待传入的参数
     * @param runTime 运行时间
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:40
     */
    public static <T> ResultModel<T> getSuccessResultAndRunTime(T data, Long runTime) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        resultModel.setData(data);
        return resultModel;
    }

    /**
     * 获取成功返回信息
     *
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:32
     */
    public static <T> ResultModel<T> getSuccessResult() {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        resultModel.setData(null);
        return resultModel;
    }

    /**
     * 根据data获取成功返回信息
     *
     * @param data 待传入的参数
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:40
     */
    public static <T> ResultModel<T> getSuccessResult(T data) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        resultModel.setData(data);
        return resultModel;
    }

    /**
     * 根据data获取成功返回信息
     *
     * @param data 待传入的参数
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:40
     */
    public static <T> ResultModel<T> getSuccessResult(T data,long runTime, long timestamp ) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        resultModel.setData(data);
        resultModel.setRunTime(runTime);
        resultModel.setTimestamp(timestamp);
        return resultModel;
    }

    /**
     * 根据data获取成功返回信息
     *
     * @param data 待传入的参数
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:40
     */
    public static <T> ResultModel<T> getSuccessResult(Integer code, T data) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(code);
        resultModel.setData(data);
        resultModel.setMessage("OK");
        return resultModel;
    }

    /**
     * 根据key，data获取成功返回信息
     *
     * @param key  key
     * @param data 待传入的参数
     * @return 成功返回信息
     * @author YuChen
     * @date 2018/6/7 9:40
     */
    public static ResultModel<JSONObject> getSuccessResult(String key, Object data) {
        ResultModel<JSONObject> resultModel = new ResultModel<>();
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, data);
        resultModel.setData(jsonObject);
        return resultModel;
    }

    /**
     * 根据code和msg返回信息
     *
     * @param code 状态码
     * @param msg  提示消息
     * @return 返回体
     * @author YuChen
     * @date 2018/6/20 20:08
     */
    public static <T> ResultModel<T> getResultByCodeAndMsg(Integer code, String msg) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.setCode(code);
        resultModel.setData(null);
        resultModel.setMessage(msg);
        return resultModel;
    }
}
