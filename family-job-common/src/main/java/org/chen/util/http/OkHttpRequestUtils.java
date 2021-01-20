package org.chen.util.http;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构造okhttp request
 *
 * @author YuChen
 */
public class OkHttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(OkHttpRequestUtils.class);

    public static void main(String[] args) throws Exception {
//        Request postRequestToHuiDao = getPostRequestToHuiDao("https://183.194.243.82/clientgateway/"
//                , "{\"uid\":\"testtest3\",\"ZJHM\":\"429001199306083819\",\"XM\":\"刘如\"}"
//                , null
//                , "46242e04-eae9-48e0-b675-2716d9b9d7e4"
//                , "0ee22476-a88c-46db-8486-b007d3d8b19f"
//                , "bfa96e69-ad6a-4719-b0dd-44fc7f2e2051");
//        OkHttpClient httpClient = OkhttpUtils.getHttpClient();
//        Response execute = httpClient.newCall(postRequestToHuiDao).execute();
//        System.out.println(execute.body().string());

        String huiDaoSignature = getHuiDaoSignature("c9071aeb-8078-43c8-8791-bf1caca85fb8", "eb3d87c7-d8b1-42a4-822c-4f3305fbb2ef", "9c4743aa-5858-4efb-9970-ffaff389dea7", 231231231);
        System.out.println(huiDaoSignature);
    }

    /**
     * 构建GET方法的请求 且将参数放到url里
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static Request getGetRequest(String url, Map<String, String> param) {
        String finUrl = concatUrlAndParam(url, param);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        return new Request.Builder().url(finUrl).build();
    }

    /**
     * 将url和参数组装
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/2/20 19:16
     */
    public static String concatUrlAndParam(String url, Map<String, String> param) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        String finUrl = url;
        if (CollectionUtil.isNotEmpty(param)) {
            if (!url.endsWith("?")) {
                finUrl += "?";
            }
            String paramInUrl = paramToUrl(param);
            finUrl += paramInUrl;
        }
        return finUrl;
    }

    /**
     * 将参数转化成URL形式
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/2/20 19:09
     */
    public static String paramToUrl(Map<String, String> param) {
        StringBuilder res = new StringBuilder();
        for (String key : param.keySet()) {
            res.append(key).append("=").append(param.get(key)).append("&");
        }
        res.deleteCharAt(res.length() - 1);
        return res.toString();
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数 实体类
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestAndJsonBody(String url, Object bodyParam, Map<String, String> urlParam) {
        RequestBody body = initRequestJsonBody(bodyParam);
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        return new Request.Builder()
                .url(finUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数 实体类
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestAndJsonBody(String url, Object bodyParam, Map<String, String> urlParam, Map<String, String> headers) {
        RequestBody body = initRequestJsonBody(bodyParam);
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        Request.Builder postBuilder = new Request.Builder()
                .url(finUrl)
                .post(body)
                .addHeader("Content-Type", "application/json");
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(postBuilder::addHeader);
        }
        return postBuilder.build();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestAndFormBody(String url, Map<String, String> bodyParam, Map<String, String> urlParam) {
        RequestBody body = initRequestFormBody(bodyParam);
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        return new Request.Builder()
                .url(finUrl)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数  已经转换的json字符串
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestAndJsonBody(String url, String bodyParam, Map<String, String> urlParam) {
        RequestBody body = initRequestJsonBody(bodyParam);
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        return new Request.Builder()
                .url(finUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    public static RequestBody initRequestJsonBody(Object param) {
        String jsonString;
        try {
            jsonString = JSONObject.toJSONString(param);
        } catch (Exception e) {
            log.warn("实体类转换成json字符串失败!", e);
            log.warn(" param:{}", param);
            jsonString = "";
        }
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(jsonType, jsonString);
    }

    public static RequestBody initRequestJsonBody(String param) {
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(jsonType, param);
    }

    public static RequestBody initRequestFormBody(Map<String, String> param) {
        String formString = paramToUrl(param);
        MediaType formType = MediaType.parse("application/x-www-form-urlencoded");
        return RequestBody.create(formType, formString);
    }

    /**
     * 生成huidao api post request
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数
     * @param appName   应用Id
     * @param appSecret 应用私钥
     * @param apiId     接口id
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestToHuiDao(String url, Object bodyParam, Map<String, String> urlParam, String appName, String appSecret, String apiId) {
        RequestBody body = initRequestJsonBody(bodyParam);
        return createHuiDaoPostRequest(url, urlParam, appName, appSecret, apiId, body);
    }

    /**
     * 生成huidao api post request
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param bodyParam json请求参数
     * @param appName   应用Id
     * @param appSecret 应用私钥
     * @param apiId     接口id
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestToHuiDao(String url, Object bodyParam, Map<String, String> urlParam, String appName, String appSecret, String apiId, Map<String, String> headers) {
        RequestBody body = initRequestJsonBody(bodyParam);
        return createHuiDaoPostRequest(url, urlParam, appName, appSecret, apiId, body, headers);
    }

    /**
     * 生成huidao api post request
     *
     * @param url             发送请求的 URL
     * @param urlParam        url请求参数
     * @param bodyParamString json请求参数
     * @param appName         应用Id
     * @param appSecret       应用私钥
     * @param apiId           接口id
     * @return 所代表远程资源的响应结果
     */
    public static Request getPostRequestToHuiDao(String url, String bodyParamString, Map<String, String> urlParam, String appName, String appSecret, String apiId) {
        RequestBody body = initRequestJsonBody(bodyParamString);
        return createHuiDaoPostRequest(url, urlParam, appName, appSecret, apiId, body);
    }

    /**
     * 生成huidao api post request
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param appName   应用Id
     * @param appSecret 应用私钥
     * @param apiId     接口id
     * @return 所代表远程资源的响应结果
     */
    private static Request createHuiDaoPostRequest(String url, Map<String, String> urlParam, String appName, String appSecret, String apiId, RequestBody body) {
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        String signature = null;
        try {
            signature = getHuiDaoSignature(appName, apiId, appSecret, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("huidao签名生成失败", e);
            signature = "";
        }
        return new Request.Builder()
                .url(finUrl)
                //.addHeader("Content-Type", "application/json")
                .addHeader("apiname", apiId)
                .addHeader("appid", appName)
                .addHeader("signature", signature)
                .post(body)
                .build();
    }

    /**
     * 生成huidao api post request
     *
     * @param url       发送请求的 URL
     * @param urlParam  url请求参数
     * @param appName   应用Id
     * @param appSecret 应用私钥
     * @param apiId     接口id
     * @return 所代表远程资源的响应结果
     */
    private static Request createHuiDaoPostRequest(String url, Map<String, String> urlParam, String appName
            , String appSecret, String apiId, RequestBody body, Map<String, String> headers) {
        String finUrl = concatUrlAndParam(url, urlParam);
        if (finUrl == null) {
            log.warn("url为空");
            return null;
        }
        String signature = null;
        try {
            signature = getHuiDaoSignature(appName, apiId, appSecret, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("huidao签名生成失败", e);
            signature = "";
        }
        Request.Builder postBuilder = new Request.Builder()
                .url(finUrl)
                //.addHeader("Content-Type", "application/json")
                .addHeader("apiname", apiId)
                .addHeader("appid", appName)
                .addHeader("signature", signature)
                .post(body);
        if (CollectionUtil.isNotEmpty(headers)) {
            headers.forEach(postBuilder::addHeader);
        }
        return postBuilder.build();
    }


    // huidao秘钥缓存 (其实没必要用concurrent 普通的也行)
    public final static ConcurrentHashMap<String, AES> HUIDAO_AES = new ConcurrentHashMap<>();

    /**
     * 获取汇道api调用签名
     *
     * @param
     * @return
     * @author YuChen
     * @date 2020/3/23 16:55
     */
    private static String getHuiDaoSignature(String appName, String apiId, String appSecret, long timeMillis) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        String stringtosign = appName + apiId + timeMillis;
        String appSecretClear = appSecret.replaceAll("-", "");
        // 随机生成密钥
        byte[] key = appSecretClear.getBytes();
        // 构建
        AES aes = HUIDAO_AES.get(appName + apiId);
        if (aes == null) {
            aes = SecureUtil.aes(key);
            HUIDAO_AES.put(appName + apiId,aes);
        }
        // 加密
        byte[] encrypt = aes.encrypt(stringtosign);
        String res = new BASE64Encoder().encode(encrypt);
        res = res.replaceAll("\r|\n", "");
        return res;
    }


}