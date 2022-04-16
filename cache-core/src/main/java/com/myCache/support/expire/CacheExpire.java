package com.myCache.support.expire;

import com.myCache.api.ICache;
import com.myCache.api.ICacheExpire;
import com.myCache.api.ICacheRemoveListener;
import com.myCache.api.ICacheRemoveListenerContext;
import com.myCache.constant.enums.CacheRemoveType;
import com.myCache.support.listener.remove.CacheRemoveListenerContext;
import com.github.houbb.heaven.util.util.CollectionUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存过期-普通策略
 *  定时过期
 * @author Jiangth
 * 
 * @param <K> key
 * @param <V> value
 */
public class CacheExpire<K,V> implements ICacheExpire<K,V> {

    /**
     * 单次清空的数量限制
     */
    private static final int LIMIT = 100;

    /**
     * 存放设定了过期时间的key的哈希表
     * 空间换时间
     */
    private final Map<K, Long> expireMap = new HashMap<>();

    /**
     * 缓存实现
     */
    private final ICache<K,V> cache;

    /**
     * 线程执行类,定义了一个线程进行任务的执行
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();


    public CacheExpire(ICache<K, V> cache) {
        this.cache = cache;
        this.init();
    }

    /**
     * 初始化任务，100ms后开始，每次间隔100ms
     */
    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExpireThread(), 100, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 定时执行的任务
     */
    private class ExpireThread implements Runnable {
        @Override
        public void run() {
            //1.判断带过期时间的key的哈希表中是否为空
            if (expireMap.isEmpty()) {
                return;
            }
            //2. 获取 key 进行处理,每次只处理limit个数据，防止处理的数据过多时间太长
            int count = 0;
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                if(count >= LIMIT) {
                    return;
                }
                expireKey(entry.getKey(), entry.getValue());
                count++;
            }
        }
    }

    @Override
    public void expire(K key, long expireAt) {
        expireMap.put(key, expireAt);
    }

    //惰性过期，在定期过期中可能达到处理上限而没有将数据删除完毕，所以需要惰性处理
    //在调用get方法时，先进入惰性处理阶段
    @Override
    public void refreshExpire(Collection<K> keyList) {
        //如果传来的key列表是空的，则直接返回
        if(CollectionUtil.isEmpty(keyList)) {
            return;
        }

        // 判断大小，小的作为外循环。一般都是过期的 keys 比较小。
        // 如果设定了过期时间的key的数量比较大，则对访问的keys进行查表，并过期超时的key
        if(keyList.size() <= expireMap.size()) {
            for(K key : keyList) {
                Long expireAt = expireMap.get(key);
                expireKey(key, expireAt);
            }
        } else {
            //如果设定了过期时间的key的数量比较小，则对所有设定过期时间的key进行查表，并过期超时的key
            for(Map.Entry<K, Long> entry : expireMap.entrySet()) {
                this.expireKey(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Long expireTime(K key) {
        return expireMap.get(key);
    }

    /**
     * 接受到key以及对应的过期时间，处理过期的key
     * @param key key
     * @param expireAt 过期时间
     */
    private void expireKey(final K key, final Long expireAt) {
        if(expireAt == null) {
            return;
        }
        //当前时间
        long currentTime = System.currentTimeMillis();
        if(currentTime >= expireAt) {
            //如果当前时间大于过期时间，将expireMap中的key删除
            expireMap.remove(key);
            // 再移除缓存中的key，后续可以通过惰性删除做补偿
            V removeValue = cache.remove(key);

            // 执行淘汰监听器，通过Context将淘汰的key、value、type传给监听器
            ICacheRemoveListenerContext<K,V> removeListenerContext = CacheRemoveListenerContext.<K,V>newInstance().key(key).value(removeValue).type(CacheRemoveType.EXPIRE.code());
            for(ICacheRemoveListener<K,V> listener : cache.removeListeners()) {
                listener.listen(removeListenerContext);
            }
        }
    }

}
