package com.myCache.support.evict;

import com.myCache.api.ICache;
import com.myCache.api.ICacheEvictContext;
import com.myCache.model.CacheEntry;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 丢弃策略-先进先出 done
 *
 * 由于先进先出不需要实现更新，则只需要实现doEvict即可
 *
 * @author Jiangth
 * 
 */
public class CacheEvictFifo<K,V> extends AbstractCacheEvict<K,V> {

    /**
     * 利用一个双端队列维护先进先出的功能
     * queue 信息
     */
    private final Queue<K> queue = new LinkedList<>();

    /**
     * 如果函数返回空，则代表没有删除信息就把新的内容放进去了
     * @param context 上下文
     * @return
     */
    @Override
    public CacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context) {
        //返回的Entry
        CacheEntry<K,V> result = null;

        final ICache<K,V> cache = context.cache();
        // 超过限制，执行移除 cache.size是当前大小  context.size是limitsize
        if(cache.size() >= context.size()) {
            K evictKey = queue.remove();
            // 移除最开始的元素
            V evictValue = cache.remove(evictKey);
            result = new CacheEntry<>(evictKey, evictValue);
        }

        // 将新加的元素放入队尾
        final K key = context.key();
        queue.add(key);
        return result;
    }

}
