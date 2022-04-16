package com.myCache.support.interceptor.rdbaof;

import com.alibaba.fastjson.JSON;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.myCache.api.ICache;
import com.myCache.api.ICacheInterceptor;
import com.myCache.api.ICacheInterceptorContext;
import com.myCache.api.ICachePersist;
import com.myCache.model.PersistAofEntry;
import com.myCache.support.persist.CachePersistAof;
import com.myCache.support.persist.CachePersistAofRdb;

/**
 * @author Jiangth
 */
public class CacheInterceptorRdbAof<K,V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(com.myCache.support.interceptor.aof.CacheInterceptorAof.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
        //将所要执行的方法、参数存储到文件中 before或after都可以 这里在after中处理
    }

    @Override
    public void after(ICacheInterceptorContext<K,V> context) {
        // 持久化类
        ICache<K,V> cache = context.cache();
        ICachePersist<K,V> persist = cache.persist();

        //如果持久化类是AOF持久化的实例，则进入
        if(persist instanceof CachePersistAofRdb) {
            CachePersistAofRdb<K,V> cachePersistAof = (CachePersistAofRdb<K,V>) persist;

            String methodName = context.method().getName();
            PersistAofEntry aofEntry = PersistAofEntry.newInstance();
            aofEntry.setMethodName(methodName);
            aofEntry.setParams(context.params());

            String json = JSON.toJSONString(aofEntry);

            // 直接持久化
            log.debug("RDBAOF 开始追加文件内容：{}", json);
            //将带有参数信息、方法名的信息的json串添加到AOF持久化的缓存区中 最后在定时任务中调用，执行缓存
            cachePersistAof.append(json);
            log.debug("RDBAOF 完成追加文件内容：{}", json);
        }
    }

}

