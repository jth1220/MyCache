package com.myCache.support.interceptor;

import com.myCache.api.ICacheInterceptor;
import com.myCache.support.interceptor.aof.CacheInterceptorAof;
import com.myCache.support.interceptor.common.CacheInterceptorCost;
import com.myCache.support.interceptor.evict.CacheInterceptorEvict;
import com.myCache.support.interceptor.rdbaof.CacheInterceptorRdbAof;
import com.myCache.support.interceptor.refresh.CacheInterceptorRefresh;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存拦截器工具类
 * @author Jiangth
 */
public final class CacheInterceptors {

    /**
     * 默认通用里头只有统计时间的拦截器
     * @return 结果
     */
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultCommonList() {
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorCost());
        return list;
    }

    /**
     * 默认刷新
     * @return 结果
     */
    @SuppressWarnings("all")
    public static List<ICacheInterceptor> defaultRefreshList() {
        List<ICacheInterceptor> list = new ArrayList<>();
        list.add(new CacheInterceptorRefresh());
        return list;
    }

    /**
     * AOF 模式
     * @return 结果
     */
    @SuppressWarnings("all")
    public static ICacheInterceptor aof() {
        return new CacheInterceptorAof();
    }

    /**
     * RDBAOF 模式
     * @return 结果
     */
    @SuppressWarnings("all")
    public static ICacheInterceptor rdbaof() {
        return new CacheInterceptorRdbAof();
    }

    /**
     * 驱除策略拦截器
     * @return 结果
     */
    @SuppressWarnings("all")
    public static ICacheInterceptor evict() {
        return new CacheInterceptorEvict();
    }

}
