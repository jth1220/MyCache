package com.myCache.support.persist;

import com.myCache.api.ICache;
import com.myCache.api.ICachePersist;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 内部缓存持久化类
 * @author Jiangth
 * @param <K> key
 * @param <V> value
 * 
 */
public class InnerCachePersist<K,V> {

    private static final Log log = LogFactory.getLog(InnerCachePersist.class);

    /**
     * 缓存信息
     */
    private final ICache<K,V> cache;

    /**
     * 缓存持久化策略
     */
    private final ICachePersist<K,V> persist;

    /**
     * 线程执行类
     */
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();

    public InnerCachePersist(ICache<K, V> cache, ICachePersist<K, V> persist) {
        this.cache = cache;
        this.persist = persist;

        // 初始化
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("开始持久化缓存信息");
                    persist.persist(cache);
                    log.info("完成持久化缓存信息");
                } catch (Exception exception) {
                    log.error("文件持久化异常", exception);
                }
            }
        }, persist.delay(), persist.period(), persist.timeUnit());
    }

}
