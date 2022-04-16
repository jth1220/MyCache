package com.myCache.support.listener.remove;

import com.myCache.api.ICacheRemoveListenerContext;

/**
 * 删除的监听器
 * @author Jiangth
 * 
 */
public class CacheRemoveListenerContext<K,V> implements ICacheRemoveListenerContext<K,V> {

    /**
     * key
     */
    private K key;

    /**
     * 值
     */
    private V value;

    /**
     * 删除类型
     */
    private String type;

    /**
     * 新建实例
     * @param <K> key
     * @param <V> value
     * @return 结果
     * 
     */
    public static <K,V> CacheRemoveListenerContext<K,V> newInstance() {
        return new CacheRemoveListenerContext<>();
    }

    @Override
    public K key() {
        return key;
    }

    public CacheRemoveListenerContext<K, V> key(K key) {
        this.key = key;
        return this;
    }

    @Override
    public V value() {
        return value;
    }

    public CacheRemoveListenerContext<K, V> value(V value) {
        this.value = value;
        return this;
    }

    @Override
    public String type() {
        return type;
    }

    public CacheRemoveListenerContext<K, V> type(String type) {
        this.type = type;
        return this;
    }
}
