package com.myCache.support.interceptor.evict;

import com.myCache.api.ICacheEvict;
import com.myCache.api.ICacheInterceptor;
import com.myCache.api.ICacheInterceptorContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

import java.lang.reflect.Method;

/**
 * 淘汰策略
 * @author Jiangth
 */
public class CacheInterceptorEvict<K,V> implements ICacheInterceptor<K, V> {

    private static final Log log = LogFactory.getLog(CacheInterceptorEvict.class);

    @Override
    public void before(ICacheInterceptorContext<K,V> context) {
    }

    //在after中执行，不然每次当前操作的key都会被放在淘汰过程的最前面，可能起不到正常淘汰的效果
    @Override
    @SuppressWarnings("all")
    public void after(ICacheInterceptorContext<K,V> context) {
        ICacheEvict<K,V> evict = context.cache().evict();

        Method method = context.method();
        final K key = (K) context.params()[0];
        if("remove".equals(method.getName())) {
            //删除的是对应在各种方法中为了减少时间复杂度所保存的信息中的key
            evict.removeKey(key);
        } else {
            evict.updateKey(key);
        }
    }

}
