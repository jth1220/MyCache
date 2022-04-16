package com.myCache.api;

/**
 * 驱除策略。
 *
 * 1.新家的key
 * 2.map实现
 * 3.淘汰监听器
 *
 * @author Jiangth
 */
public interface ICacheEvictContext<K,V> {
    /**
     * 新加的 key
     * @return key
     */
    K key();

    /**
     * cache 实现
     * @return map
     */
    ICache<K, V> cache();

    /**
     * 获取大小
     * @return 大小
     */
    int size();
}
