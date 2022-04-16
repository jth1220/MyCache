package com.myCache.support.load;

import com.myCache.api.ICache;
import com.myCache.api.ICacheLoad;

/**
 * 加载策略-无
 * @author Jiangth
 * 
 */
public class CacheLoadNone<K,V> implements ICacheLoad<K,V> {

    @Override
    public void load(ICache<K, V> cache) {
        //nothing...
    }

}
