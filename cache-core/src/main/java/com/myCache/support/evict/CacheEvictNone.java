package com.myCache.support.evict;

import com.myCache.api.ICacheEntry;
import com.myCache.api.ICacheEvictContext;

/**
 * 丢弃策略
 * @author Jiangth
 * 
 */
public class CacheEvictNone<K,V> extends AbstractCacheEvict<K,V> {

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        return null;
    }

}
