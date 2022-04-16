package com.myCache.api;

/**
 * 拦截器接口
 * @author Jiangth
 */
public interface ICacheInterceptor<K,V> {

    /**
     * 方法执行之前
     * @param context 上下文
     */
    void before(ICacheInterceptorContext<K,V> context);

    /**
     * 方法执行之后
     * @param context 上下文
     */
    void after(ICacheInterceptorContext<K,V> context);
}
