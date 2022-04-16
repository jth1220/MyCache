package com.myCache.core;

import com.myCache.annotation.CacheInterceptor;
import com.myCache.api.*;
import com.myCache.constant.enums.CacheRemoveType;
import com.myCache.exception.CacheRuntimeException;
import com.myCache.support.evict.CacheEvictContext;
import com.myCache.support.expire.CacheExpire;
import com.myCache.support.expire.CacheExpireRandom;
import com.myCache.support.listener.remove.CacheRemoveListenerContext;
import com.myCache.support.persist.InnerCachePersist;
import com.myCache.support.proxy.CacheProxy;
import com.github.houbb.heaven.util.lang.ObjectUtil;


import java.util.*;

/**
 * 缓存信息
 *
 * @author Jiangth
 * @param <K> key
 * @param <V> value
 * 
 */
public class Cache<K,V> implements ICache<K,V> {

    /**
     * map 信息
     */
    private Map<K,V> map;

    /**
     * 大小限制
     */
    private int sizeLimit;

    /**
     * 淘汰策略
     */
    private ICacheEvict<K,V> evict;

    /**
     * 过期策略
     */
    private ICacheExpire<K,V> expire;

    /**
     * 删除监听类
     */
    private List<ICacheRemoveListener<K,V>> removeListeners;

    /**
     * 慢日志监听类
     */
    private List<ICacheSlowListener> slowListeners;

    /**
     * 加载类
     */
    private ICacheLoad<K,V> load;

    /**
     * 持久化
     */
    private ICachePersist<K,V> persist;

    /**
     * 设置 map
     * @param map
     * @return this
     */
    public Cache<K, V> map(Map<K, V> map) {
        this.map = map;
        return this;
    }

    /**
     * 设置大小限制
     * @param sizeLimit 大小限制
     * @return this
     */
    public Cache<K, V> sizeLimit(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        return this;
    }

    /**
     * 设置淘汰策略
     * @param cacheEvict 淘汰策略
     * @return this
     * 
     */
    public Cache<K, V> evict(ICacheEvict<K, V> cacheEvict) {
        this.evict = cacheEvict;
        return this;
    }

    /**
     * 获取持久化类
     * @return 持久化
     */
    @Override
    public ICachePersist<K, V> persist() {
        return persist;
    }


    /**
     * 获取淘汰策略
     * @return 驱除策略
     */
    @Override
    public ICacheEvict<K, V> evict() {
        return this.evict;
    }

    public Cache<K, V> expire(ICacheExpire<K,V> expire) {
        this.expire = expire;
        return this;
    }

    /**
     * 设置持久化策略
     * @param persist 持久化
     */
    public void persist(ICachePersist<K, V> persist) {
        this.persist = persist;
    }

    /**
     * 获取删除监听器
     * @return
     */
    @Override
    public List<ICacheRemoveListener<K, V>> removeListeners() {
        return removeListeners;
    }

    /**
     * 设置删除监听器
     * @param removeListeners
     * @return
     */
    public Cache<K, V> removeListeners(List<ICacheRemoveListener<K, V>> removeListeners) {
        this.removeListeners = removeListeners;
        return this;
    }


    /**
     * 获取慢监听器
     * @return
     */
    @Override
    public List<ICacheSlowListener> slowListeners() {
        return slowListeners;
    }

    /**
     * 设置慢监听器
     * @param slowListeners
     * @return
     */
    public Cache<K, V> slowListeners(List<ICacheSlowListener> slowListeners) {
        this.slowListeners = slowListeners;
        return this;
    }

    /**
     * 获取加载类
     * @return
     */
    @Override
    public ICacheLoad<K, V> load() {
        return load;
    }

    /**
     * 设置加载类
     * @param load
     * @return
     */
    public Cache<K, V> load(ICacheLoad<K, V> load) {
        this.load = load;
        return this;
    }

    /**
     * 初始化,初始化
     */
    public void init() {
        // 默认开启定时过期
        this.expire = new CacheExpireRandom<>(this);

        // 加载数据
        this.load.load(this);
        // 初始化持久化，默认无持久化，如果设置持久化则为定时任务，定时json或者aop方式持久化
        if(this.persist != null) {
            new InnerCachePersist<>(this, persist);
        }
    }

    /**
     * 设置key过期时间
     * @param key
     * @param timeInMills 毫秒时间之后过期
     * @return this
     */
    @Override
    @CacheInterceptor
    public ICache<K, V> expire(K key, long timeInMills) {
        //计算出过期时间，过期时间为当前时间加存活长度
        long expireTime = System.currentTimeMillis() + timeInMills;

        // 使用代理调用设置过期时间
        Cache<K,V> cachePoxy = (Cache<K, V>) CacheProxy.getProxy(this);
        return cachePoxy.expireAt(key, expireTime);
    }

    /**
     * 指定过期信息
     * @param key key
     * @param timeInMills 时间戳
     * @return this
     */
    @Override
    @CacheInterceptor(aof = true,rdbaof = true)
    public ICache<K, V> expireAt(K key, long timeInMills) {
        this.expire.expire(key, timeInMills);
        return this;
    }

    @Override
    @CacheInterceptor
    public ICacheExpire<K, V> expire() {
        return this.expire;
    }

    @Override
    @CacheInterceptor(refresh = true)
    public int size() {
        return map.size();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @CacheInterceptor(refresh = true, evict = true)
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @CacheInterceptor(refresh = true)
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @CacheInterceptor(evict = true)
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        //1. 惰性处理key
        K genericKey = (K) key;
        this.expire.refreshExpire(Collections.singletonList(genericKey));
        //2. 惰性处理完后返回key
        return map.get(key);
    }

    //put方法
    @Override
    @CacheInterceptor(aof = true, evict = true,rdbaof = true)
    public V put(K key, V value) {
        //1 尝试淘汰
        CacheEvictContext<K,V> context = new CacheEvictContext<>();
        context.key(key).size(sizeLimit).cache(this);

        //接收到了一个淘汰的元素，如果是null 说明没有被淘汰的元素
        ICacheEntry<K,V> evictEntry = evict.evict(context);

        // 添加拦截器调用，如果有淘汰的元素是进入判断
        if(ObjectUtil.isNotNull(evictEntry)) {
            // 执行淘汰监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(evictEntry.key())
                    .value(evictEntry.value())
                    .type(CacheRemoveType.EVICT.code());
            //注册的淘汰监听器监听到了remove信息，并打印出来
            for(ICacheRemoveListener<K,V> listener : context.cache().removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }

        //2. 判断驱除后的信息，如果还是满，则抛出异常，否则添加并返回最新的map
        if(isSizeLimit()) {
            throw new CacheRuntimeException("当前队列已满，数据添加失败！");
        }

        //3. 执行添加
        return map.put(key, value);
    }

    /**
     * 是否已经达到大小最大的限制
     * @return 是否达到限制
     */
    private boolean isSizeLimit() {
        final int currentSize = this.size();
        return currentSize >= this.sizeLimit;
    }

    @Override
    @CacheInterceptor(aof = true, evict = true,rdbaof = true)
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    @CacheInterceptor(aof = true,rdbaof = true)
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    @CacheInterceptor(refresh = true, aof = true,rdbaof = true)
    public void clear() {
        map.clear();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @CacheInterceptor(refresh = true)
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

}
