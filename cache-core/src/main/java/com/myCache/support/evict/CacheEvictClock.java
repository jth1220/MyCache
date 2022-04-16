package com.myCache.support.evict;

import com.myCache.api.ICache;
import com.myCache.api.ICacheEntry;
import com.myCache.api.ICacheEvictContext;
import com.myCache.model.CacheEntry;
import com.myCache.support.struct.lru.ILruMap;
import com.myCache.support.struct.lru.impl.LruMapCircleList;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 淘汰策略-clock算法（NRU 最近未用算法） done
 * @author Jiangth
 */
public class CacheEvictClock<K,V> extends AbstractCacheEvict<K,V> {

    private static final Log log = LogFactory.getLog(CacheEvictClock.class);

    /**
     * 循环链表
     */
    private final ILruMap<K,V> circleList;

    public CacheEvictClock() {
        this.circleList = new LruMapCircleList<>();
    }

    @Override
    protected ICacheEntry<K, V> doEvict(ICacheEvictContext<K, V> context) {
        ICacheEntry<K, V> result = null;
        final ICache<K,V> cache = context.cache();
        // 超过限制，移除队尾的元素
        if(cache.size() >= context.size()) {
            ICacheEntry<K,V>  evictEntry = circleList.removeEldest();;
            // 执行缓存移除操作
            final K evictKey = evictEntry.key();
            V evictValue = cache.remove(evictKey);

            log.debug("基于 clock 算法淘汰 key：{}, value: {}", evictKey, evictValue);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        return result;
    }


    /**
     * 更新信息
     * @param key 元素
     */
    @Override
    public void updateKey(final K key) {
        this.circleList.updateKey(key);
    }

    /**
     * 移除元素
     * @param key 元素
     */
    @Override
    public void removeKey(final K key) {
        this.circleList.removeKey(key);
    }

}
