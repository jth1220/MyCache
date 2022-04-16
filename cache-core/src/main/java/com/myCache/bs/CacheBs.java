package com.myCache.bs;

import com.myCache.api.*;
import com.myCache.core.Cache;
import com.myCache.support.evict.CacheEvicts;
import com.myCache.support.listener.remove.CacheRemoveListeners;
import com.myCache.support.listener.slow.CacheSlowListeners;
import com.myCache.support.load.CacheLoads;
import com.myCache.support.persist.CachePersists;
import com.myCache.support.proxy.CacheProxy;
import com.github.houbb.heaven.util.common.ArgUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存引导类，构造器采用fluent流式写法，
 * 1.在每个函数的返回处写为return this 函数体中为参数赋值，
 * 2.调用cache中的init方法，实现加载与备份方法
 * 3.最后build的过程中调用相应的构造方法
 * @author Jiangth
 *
 * 默认：HashMap、Integer.MAX_VALUE大小、FIFO淘汰、默认监听类、无慢操作过程监听类、无加载、无持久化
 *
 */
public final class CacheBs<K,V> {

    private CacheBs(){}

    /**
     * 创建对象实例
     * @param <K> key
     * @param <V> value
     * @return this
     * 
     */
    public static <K,V> CacheBs<K,V> newInstance() {
        return new CacheBs<>();
    }

    /**
     * map 实现
     */
    private Map<K,V> map = new HashMap<>();

    /**
     * 大小限制
     */
    private int size = Integer.MAX_VALUE;

    /**
     * 淘汰策略
     */
    private ICacheEvict<K,V> evict = CacheEvicts.fifo();

    /**
     * 删除过程监听类
     */
    private final List<ICacheRemoveListener<K,V>> removeListeners = CacheRemoveListeners.defaults();

    /**
     * 慢操作过程监听类
     */
    private final List<ICacheSlowListener> slowListeners = CacheSlowListeners.none();

    /**
     * 加载策略
     */
    private ICacheLoad<K,V> load = CacheLoads.none();

    /**
     * 持久化策略
     */
    private ICachePersist<K,V> persist = CachePersists.none();

    /**
     * map 实现
     * @param map map
     * @return this
     */
    public CacheBs<K, V> map(Map<K, V> map) {
        ArgUtil.notNull(map, "map");

        this.map = map;
        return this;
    }

    /**
     * 设置 size 信息
     * @param size size
     * @return this
     */
    public CacheBs<K, V> size(int size) {
        ArgUtil.notNegative(size, "size");

        this.size = size;
        return this;
    }

    /**
     * 设置驱除策略
     * @param evict 驱除策略
     * @return this
     * 
     */
    public CacheBs<K, V> evict(ICacheEvict<K, V> evict) {
        ArgUtil.notNull(evict, "evict");

        this.evict = evict;
        return this;
    }

    /**
     * 设置加载
     * @param load 加载
     * @return this
     */
    public CacheBs<K, V> load(ICacheLoad<K, V> load) {
        ArgUtil.notNull(load, "load");

        this.load = load;
        return this;
    }

    /**
     * 添加删除监听器
     * @param removeListener 监听器
     * @return this
     * 
     */
    public CacheBs<K, V> addRemoveListener(ICacheRemoveListener<K,V> removeListener) {
        ArgUtil.notNull(removeListener, "removeListener");

        this.removeListeners.add(removeListener);
        return this;
    }

    /**
     * 添加慢日志监听器
     * @param slowListener 监听器
     * @return this
     * 
     */
    public CacheBs<K, V> addSlowListener(ICacheSlowListener slowListener) {
        ArgUtil.notNull(slowListener, "slowListener");

        this.slowListeners.add(slowListener);
        return this;
    }

    /**
     * 设置持久化策略
     * @param persist 持久化
     * @return this
     * 
     */
    public CacheBs<K, V> persist(ICachePersist<K, V> persist) {
        this.persist = persist;
        return this;
    }

    /**
     * 构建缓存信息
     * @return 缓存信息
     */
    public ICache<K,V> build() {
        Cache<K,V> cache = new Cache<>();
        cache.map(map);
        cache.evict(evict);
        cache.sizeLimit(size);
        cache.removeListeners(removeListeners);
        cache.load(load);
        cache.persist(persist);
        cache.slowListeners(slowListeners);
        // 初始化数据
        cache.init();

        return CacheProxy.getProxy(cache);
    }

}
