package org.chen.util;

/**
 * 通过threadlocal储存request数据
 *  
 * @author YuChen
 * @date 2020/5/28 13:05
 **/

public class RequestHolder {

    private final static ThreadLocal<Object> REQUEST_HOLDER = new ThreadLocal<>();

    public static void add(Object obj) {
        REQUEST_HOLDER.set(obj);
    }

    public static Object getObj() {
        return REQUEST_HOLDER.get();
    }

    public static void remove() {
        REQUEST_HOLDER.remove();
    }
}