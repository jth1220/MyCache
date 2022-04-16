package com.myCache.api;

import java.util.List;
import java.util.Map;

/**
 * @author Jiangth
 */
public interface ICache<K,V> extends Map<K,V> {

    /**
     * 设置过期时间
     * @param key
     * @param timeInMills
     * @return
     */
    ICache<K,V> expire(final K key,final long timeInMills);

    /**
     * 设置在指定的时间过期
     * @param key
     * @param timeInMills
     * @return
     */
    ICache<K,V> expireAt(final K key,final long timeInMills);


    /**
     * 获取缓存的过期处理类
     * @return 处理类实现
     */
    ICacheExpire<K,V> expire();

    /**
     * 删除监听类列表
     * @return 监听器列表
     */
    List<ICacheRemoveListener<K,V>> removeListeners();

    /**
     * 慢日志监听类列表
     * @return 监听器列表
     */
    List<ICacheSlowListener> slowListeners();

    /**
     * 加载信息
     * @return 加载信息
     */
    ICacheLoad<K,V> load();

    /**
     * 持久化类
     * @return 持久化类
     */
    ICachePersist<K,V> persist();

    /**
     * 淘汰策略
     * @return 淘汰
     */
    ICacheEvict<K,V> evict();

}
