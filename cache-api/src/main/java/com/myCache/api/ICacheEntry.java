package com.myCache.api;

/**
 * 缓存明细信息
 * @author Jiangth
 */
public interface ICacheEntry<K, V> {

    /**
     * @return key
     */
    K key();

    /**
     * @return value
     */
    V value();
}
