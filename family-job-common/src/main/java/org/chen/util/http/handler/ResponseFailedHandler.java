package org.chen.util.http.handler;

import okhttp3.Response;

/**
 * json序列化失败处理器
 *  
 * @author YuChen
 * @date 2020/4/17 14:53
 **/
 
public interface ResponseFailedHandler<E> {
    E handle(String jsonStr, Response response);
}
