package com.myCache.api;

/**
 * @author Jiangth
 */
public interface ICacheEvict<K, V> {

    /**
     * 驱除策略
     *
     * @param context 上下文
     * @return 被移除的明细，没有时返回 null
     */
    ICacheEntry<K,V> evict(final ICacheEvictContext<K, V> context);

    /**
     * 更新 key 信息
     * @param key key
     */
    void updateKey(final K key);

    /**
     * 删除 key 信息
     * @param key key
     */
    void removeKey(final K key);
}
