package com.myCache.api;

import java.lang.reflect.Method;

/**
 * 拦截器上下文接口
 * @author Jiangth
 */
public interface ICacheInterceptorContext<K,V> {

    /**
     * 缓存信息
     * @return 缓存信息
     */
    ICache<K,V> cache();

    /**
     * 执行的方法信息
     * @return 方法
     */
    Method method();

    /**
     * 执行的参数
     * @return 参数
     */
    Object[] params();

    /**
     * 方法执行的结果
     * @return 结果
     */
    Object result();

    /**
     * 开始时间
     * @return 时间
     */
    long startMills();

    /**
     * 结束时间
     * @return 时间
     */
    long endMills();

}
