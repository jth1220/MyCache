package com.myCache.support.persist;

import com.myCache.api.ICache;
import com.myCache.api.ICachePersist;

import java.util.concurrent.TimeUnit;

/**
 * 缓存持久化-适配器模式
 * @author Jiangth
 */
public class CachePersistAdaptor<K,V> implements ICachePersist<K,V> {

    /**
     * 持久化
     * key长度 key+value
     * 第一个空格，获取 key 的长度，然后截取
     * @param cache 缓存
     */
    @Override
    public void persist(ICache<K, V> cache) {
    }

    @Override
    public long delay() {
        return this.period();
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }

}
