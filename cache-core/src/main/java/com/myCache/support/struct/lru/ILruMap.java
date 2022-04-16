package com.myCache.support.struct.lru;

import com.myCache.api.ICacheEntry;

/**
 * LRU map 接口
 * @author Jiangth
 */
public interface ILruMap<K,V> {

    /**
     * 移除最老的元素
     * @return 移除的明细
     * 3
     */
    ICacheEntry<K, V> removeEldest();

    /**
     * 更新 key 的信息
     * @param key key
     * 3
     */
    void updateKey(final K key);

    /**
     * 移除对应的 key 信息
     * @param key key
     * 3
     */
    void removeKey(final K key);

    /**
     * 是否为空
     * @return 是否
     * 3
     */
    boolean isEmpty();

    /**
     * 是否包含元素
     * @param key 元素
     * @return 结果
     * 3
     */
    boolean contains(final K key);

}
