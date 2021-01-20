package org.chen.rpc.responsehandler;

import lombok.extern.slf4j.Slf4j;
import org.chen.domain.dto.weixin.GetAccessTokenDto;
import org.chen.util.http.OkHttpResponseUtils;
import org.chen.util.http.handler.DefaultBusinessCodeHandler;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/26 17:43
 **/

@Slf4j
public class WeixinAccessRespHandler extends DefaultBusinessCodeHandler<GetAccessTokenDto,Object> {

    @Override
    public void handle(OkHttpResponseUtils.BaseErrorHandleResult baseErrorHandleResult, OkHttpResponseUtils.JsonResult<GetAccessTokenDto,Object> allResult, Object param, String apiName) {
        String code = getCode(allResult);
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
    public String getCode(OkHttpResponseUtils.JsonResult<GetAccessTokenDto,Object> allResult) {
        GetAccessTokenDto jsonRes = allResult.getJsonRes();
        return jsonRes.getErrcode();
    }

    @Override
    public String getMsg(OkHttpResponseUtils.JsonResult<GetAccessTokenDto,Object> allResult) {
        GetAccessTokenDto jsonRes = allResult.getJsonRes();
        return jsonRes.getErrmsg();
    }

    @Override
    public Boolean checkCode(String code) {
        // 微信没有返回code  不知道是不是bug
        // https://developers.weixin.qq.com/community/develop/doc/000ac841dac318aaa2f8bb6865b000
        return "0".equals(code) || null == code;
    }
}
