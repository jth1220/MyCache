package com.myCache.support.load;

import com.myCache.api.ICacheLoad;

/**
 * 加载策略工具类
 * @author Jiangth
 */
public final class CacheLoads {

    private CacheLoads(){}

    /**
     * 无加载
     * @param <K> key
     * @param <V> value
     * @return 值
     */
    public static <K,V> ICacheLoad<K,V> none() {
        return new CacheLoadNone<>();
    }

    /**
     * 文件 JSON
     * @param dbPath 文件路径
     * @param <K> key
     * @param <V> value
     * @return 值
     */
    public static <K,V> ICacheLoad<K,V> dbJson(final String dbPath) {
        return new CacheLoadDbJson<>(dbPath);
    }

    /**
     * AOF 文件加载模式
     * @param dbPath 文件路径
     * @param <K> key
     * @param <V> value
     * @return 值
     */
    public static <K,V> ICacheLoad<K,V> aof(final String dbPath) {
        return new CacheLoadAof<>(dbPath);
    }


    /**
     * RDBAOF 文件加载模式
     * @param dbPath 文件路径
     * @param <K> key
     * @param <V> value
     * @return 值
     */
    public static <K,V> ICacheLoad<K,V> rdbaof(final String dbPath) {
        return new CacheLoadAofRdb<>(dbPath);
    }

}
