package com.myCache.support.listener.slow;

import com.alibaba.fastjson.JSON;
import com.myCache.api.ICacheSlowListener;
import com.myCache.api.ICacheSlowListenerContext;
import com.myCache.support.interceptor.common.CacheInterceptorCost;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;

/**
 * 慢日志监听类 慢日志的效果是监听cache操作中的耗时，当耗时超过时间阈值时，调用满操作监听器
 * @author Jiangth
 */
public class CacheSlowListener implements ICacheSlowListener {

    private static final Log log = LogFactory.getLog(CacheInterceptorCost.class);

    @Override
    public void listen(ICacheSlowListenerContext context) {
        log.warn("[Slow] methodName: {}, params: {}, cost time: {}",
                context.methodName(), JSON.toJSON(context.params()), context.costTimeMills());
    }

    @Override
    public long slowerThanMills() {
        return 1000L;
    }

}
