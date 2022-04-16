package com.myCache.api;

import java.util.Collection;
import java.util.List;

/**
 * 缓存过期接口
 * @author Jiangth
 */
public interface ICacheExpire<K,V> {

    /**
     * 设置过期信息
     * @param key
     * @param expireAt
     */
    void expire(final K key,final long expireAt);

    /**
     * 惰性删除需要处理的keys
     * @param keyList
     */
    void refreshExpire(final Collection<K> keyList);

    /**
     * 返回key的过期时间
     * 不存在则返回null
     * @param key
     * @return 过期时间
     */
    Long expireTime(final K key);

}
