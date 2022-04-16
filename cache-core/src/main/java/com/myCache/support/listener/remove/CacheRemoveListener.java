package com.myCache.support.listener.remove;

import com.myCache.api.ICacheRemoveListener;
import com.myCache.api.ICacheRemoveListenerContext;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 默认的删除监听类
 * @author Jiangth
 */
public class CacheRemoveListener<K,V> implements ICacheRemoveListener<K,V> {

    private static final Log log = LogFactory.getLog(CacheRemoveListener.class);

    @Override
    public void listen(ICacheRemoveListenerContext<K, V> context) {
        log.debug("Remove key: {}, value: {}, type: {}",
                context.key(), context.value(), context.type());
    }

}
