package org.chen.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.tomcat.util.codec.binary.Base64;
import org.chen.constant.BusinessConstant;
import org.chen.domain.dto.weixin.WeixinSessionDto;
import org.chen.rpc.responsehandler.WeixinRespnseHandler;
import org.chen.util.http.OkHttpRequestUtils;
import org.chen.util.http.OkHttpResponseUtils;
import org.chen.util.http.OkhttpUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：腼腆的老黄.
 * @date ：Created in 2019-05-06 21:24
 */
@Slf4j
public class WeChatMiniProgramUtil {

    public static void main(String[] args) {
//        String session = jsCode2Session(WeChatMiniAppsConstant.APP_ID, WeChatMiniAppsConstant.APP_SECRET, "061qZd2h1wnFQs0CbK0h1aGm2h1qZd2A");
//
//        String sessionKey="OJg0yDo3KfFA/Bx8MlG89A==";
//        String encryptedData="L5BSapd9MwvOSdmVajGfABkWTvRrxibWBHsjXz4XzkfE6+1aKvHDNRA2W4KTTqwcvCQek3rlwlHAQYNj4706OnKNQ5cFaKHuQlKJxgbzcKfWBtEsIbrARF5/FqYG2dUSUNSWwwkJ58JoT6XAHumtY2xyNeTUsCu+Hw3eLFOpxMh6jGCCfU0jtRFUQGS328X9t6TiVVb/GsYEPHThAz9vRdb1b/eCY7JOPEHMMeuweK4EZizxoLvWkGySNOtWkBph6UItms0TxaTNGSf7ueELJsyt5Oj9FyqupLx7f+MySuNy1y2kkrEZoZQbyLdrMShhsoRwahBd88HgAmKHnwkpF2Od0NDjpZD2zFKXKd/l9m4CksGwvG/CCMg1xUbZj46KjIDyhReHv0IUyE3en7sdIXOqmAIatbHxXO+ZkfWFBnhoOY+kXMrFrd2htY8GueoGR0U22XHWdcUdw8AkkkHjA02hvaWUFhpQnJo1oHFG9uSzHTVwRwB+AwelXmmSSAoEtVgL7iZAAG9+gbb3FLNynbVBj0/mn4+mJmEm+hAtz3w=";
//        String iv="ZtnLAWKkyI2RIZ93Dj7FUA==";
//        WeChatMiniProgramUserInfo decryptUserInfo = decryptUserInfo(encryptedData, sessionKey, iv);
//        System.out.println(decryptUserInfo);
    }


    /**
     * 小程序jsCode2Session
     *
     * @param appId
     * @param secret
     * @param jsCode
     * @return
     */
    public static WeixinSessionDto jsCode2Session(String appId, String secret, String jsCode) {
        //可以用map的形式去请求
        StringBuilder sb = new StringBuilder("");
        Map<String, String> urlParam = new HashMap<>();
        urlParam.put("appid", appId);
        urlParam.put("secret", secret);
        urlParam.put("js_code", jsCode);
        urlParam.put("grant_type", "authorization_code");
        OkHttpClient httpClient = OkhttpUtils.getHttpClient();
        Request request = OkHttpRequestUtils.getGetRequest(BusinessConstant.JSCODE2SESSION, urlParam);
        OkHttpResponseUtils.JsonResult<WeixinSessionDto, Object> jscode2session = OkHttpResponseUtils.getResultFromJsonBody(request, WeixinSessionDto.class
                , null, false, false, "jscode2session");
        OkHttpResponseUtils.baseErrorHandle(jscode2session, new WeixinRespnseHandler(), urlParam, "jscode2session");
        return jscode2session.getJsonRes();
    }

    public static WeChatMiniProgramMobile decryptMobile(String encryptedData, String sessionKey, String iv) {
        String data = decryptData(encryptedData, sessionKey, iv);
        if (null != data) {
            WeChatMiniProgramMobile info = JSONObject.parseObject(data, WeChatMiniProgramMobile.class);
            return info;
        }
        return null;
    }

    /**
     * 解密用户信息
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    public static<T> T decryptUserInfo(String encryptedData, String sessionKey, String iv, Class<T> clazz) {

        String data = decryptData(encryptedData, sessionKey, iv);
        if (null != data) {
            return JSONObject.parseObject(data, clazz);
        }
        return null;
    }

    private static String decryptData(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] decrypt = AES.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
            if (null != decrypt && decrypt.length > 0) {
                return new String(decrypt, StandardCharsets.UTF_8);
            } else {
                return null;
            }
        } catch (Exception e) {
            //工具类不往外抛异常,返回个空即可
            // 业务层去处理异常情况
            log.error("微信信息解密失败",e);
            return null;
        }
    }


    public static class WeChatMiniProgramMobile {


        /**
         * phoneNumber : 15258801811
         * watermark : {"appid":"wxc1a151d84acc4741","timestamp":1557374219}
         * purePhoneNumber : 15258801811
         * countryCode : 86
         */
        private String phoneNumber;
        private WatermarkEntity watermark;
        private String purePhoneNumber;
        private String countryCode;

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setWatermark(WatermarkEntity watermark) {
            this.watermark = watermark;
        }

        public void setPurePhoneNumber(String purePhoneNumber) {
            this.purePhoneNumber = purePhoneNumber;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public WatermarkEntity getWatermark() {
            return watermark;
        }

        public String getPurePhoneNumber() {
            return purePhoneNumber;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public class WatermarkEntity {
            /**
             * appid : wxc1a151d84acc4741
             * timestamp : 1557374219
             */
            private String appid;
            private int timestamp;

            public void setAppid(String appid) {
                this.appid = appid;
            }

            public void setTimestamp(int timestamp) {
                this.timestamp = timestamp;
            }

            public String getAppid() {
                return appid;
            }

            public int getTimestamp() {
                return timestamp;
            }
        }
    }
}
