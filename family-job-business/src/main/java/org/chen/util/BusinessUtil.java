package org.chen.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import okhttp3.Request;
import org.chen.constant.BusinessConstant;
import org.chen.domain.dto.weixin.SendMsgDto;
import org.chen.domain.dto.weixin.SendMsgParamDto;
import org.chen.rpc.responsehandler.WeixinSendRespnseHandler;
import org.chen.timer.UpdateTimer;
import org.chen.util.http.OkHttpRequestUtils;
import org.chen.util.http.OkHttpResponseUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 *  
 * @author ObserverYu
 * @date 2020/11/26 16:06
 **/
 
public class BusinessUtil {


    /**
     * 生成交易订单号
     *
     * @return 交易订单号
     * @author liyuan
     * @date 2018/3/27 17:30
     */
    public static String getOrderNumber(String prefix) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return prefix + format.format(new Date()) + RandomUtil.randomNumbers(4);
    }

    /**
     * 生成邀请码
     *
     * @return 交易订单号
     * @author liyuan
     * @date 2018/3/27 17:30
     */
    public static String getInviteCode() {
        return RandomUtil.randomString(8);
    }


    public static SendMsgDto sendWeixinMsg(String token, String touser, String templateId
            , String page, JSONObject data){
        String url = BusinessConstant.SUBSCRIBE_MESSAGE_SEND + "?access_token="+token;
        SendMsgParamDto param = new SendMsgParamDto();
        //param.setMiniprogram_state("trial");
        param.setData(data);
        param.setPage(page);
        param.setTemplate_id(templateId);
        param.setTouser(touser);
        Request request = OkHttpRequestUtils.getPostRequestAndJsonBody(url, param, null);
        OkHttpResponseUtils.JsonResult<SendMsgDto, Object> result = OkHttpResponseUtils.getResultFromJsonBody(request, SendMsgDto.class, null, false);
        OkHttpResponseUtils.baseErrorHandle(result,new WeixinSendRespnseHandler(),param,"发送订阅消息");
        return result.getJsonRes();
    }

    public static SendMsgDto sendCronJobMsg(String token, String touser, Integer dayCount,Integer weekCount,
                                            Long point){
        JSONObject jsonObject = new JSONObject();
        String title = "您有"+dayCount+"个每日家务,"+weekCount+"个每周家务等待完成";
        String desc = "完成最多可获得"+point+"家务点";
        String remark = "每日家务次日过期,每周家务次周一过期";
        jsonObject.set("thing5",new JSONObject().set("value",title));
        jsonObject.set("thing2",new JSONObject().set("value",desc));
        jsonObject.set("thing8",new JSONObject().set("value",remark));
        return sendWeixinMsg(token,touser,UpdateTimer.getTemplateId(),"/pages/job-list/job-list?isMineJob=1&doRefresh=1&state=1",jsonObject);
    }

    public static void main(String[] args){

    }

}
