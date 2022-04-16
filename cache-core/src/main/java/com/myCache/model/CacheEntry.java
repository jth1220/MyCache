package com.myCache.model;

import com.myCache.api.ICacheEntry;

/**
 * key value 的明细
 * @author Jiangth
 * @param <K> key
 * @param <V> value
 */
public class CacheEntry<K,V> implements ICacheEntry<K,V> {

    /**
     * key
     */
    private final K key;

    /**
     * value
     */
    private final V value;

    /**
     * 新建元素
     * @param key key
     * @param value value
     * @param <K> 泛型
     * @param <V> 泛型
     * @return 结果
     * 1
     */
    public static <K,V> CacheEntry<K,V> of(final K key,
                                           final V value) {
        return new CacheEntry<>(key, value);
    }

    public CacheEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public String toString() {
        return "EvictEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

}
