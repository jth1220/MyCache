package com.myCache.support.persist;

import com.myCache.api.ICachePersist;

/**
 * 缓存持久化工具类
 * @author Jiangth
 */
public final class CachePersists {

    private CachePersists(){}

    /**
     * 无操作
     * @param <K> key
     * @param <V> value
     * @return 结果
     */
    public static <K,V> ICachePersist<K,V> none() {
        return new CachePersistNone<>();
    }

    /**
     * DB json 操作
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     */
    public static <K,V> ICachePersist<K,V> dbJson(final String path) {
        return new CachePersistDbJson<>(path);
    }

    /**
     * AOF 持久化
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     */
    public static <K,V> ICachePersist<K,V> aof(final String path) {
        return new CachePersistAof<>(path);
    }

    /**
     * RDB_AOF 持久化
     * @param <K> key
     * @param <V> value
     * @param path 文件路径
     * @return 结果
     */
    public static <K,V> ICachePersist<K,V> rdbaof(final String path,final int num) {
        return new CachePersistAofRdb<>(path,num);
    }
}
