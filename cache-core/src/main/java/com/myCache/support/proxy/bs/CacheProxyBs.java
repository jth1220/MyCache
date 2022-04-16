package com.myCache.support.proxy.bs;

import com.myCache.annotation.CacheInterceptor;
import com.myCache.api.ICache;
import com.myCache.api.ICacheInterceptor;
import com.myCache.api.ICachePersist;
import com.myCache.support.interceptor.CacheInterceptorContext;
import com.myCache.support.interceptor.CacheInterceptors;
import com.myCache.support.persist.CachePersistAof;
import com.myCache.support.persist.CachePersistAofRdb;

import java.util.List;

/**
 * 代理引导类
 * @author Jiangth
 */
public final class CacheProxyBs {

    private CacheProxyBs(){}

    /**
     * 代理上下文
     */
    private ICacheProxyBsContext context;

    /**
     * 默认通用拦截器
     *
     * JDK 的泛型擦除导致这里不能使用泛型
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> commonInterceptors = CacheInterceptors.defaultCommonList();

    /**
     * 默认刷新拦截器
     */
    @SuppressWarnings("all")
    private final List<ICacheInterceptor> refreshInterceptors = CacheInterceptors.defaultRefreshList();

    /**
     * 持久化拦截器
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor persistInterceptors = CacheInterceptors.aof();

    private final ICacheInterceptor persistInterceptors2 = CacheInterceptors.rdbaof();

    /**
     * 驱除拦截器
     */
    @SuppressWarnings("all")
    private final ICacheInterceptor evictInterceptors = CacheInterceptors.evict();

    /**
     * 新建对象实例
     * @return 实例
     */
    public static CacheProxyBs newInstance() {
        return new CacheProxyBs();
    }

    public CacheProxyBs context(ICacheProxyBsContext context) {
        this.context = context;
        return this;
    }

    /**
     * 执行
     * @return 结果
     * 
     * @throws Throwable 异常
     */
    @SuppressWarnings("all")
    public Object execute() throws Throwable {
        //1. 开始的时间
        final long startMills = System.currentTimeMillis();
        final ICache cache = context.target();
        CacheInterceptorContext interceptorContext = CacheInterceptorContext.newInstance()
                .startMills(startMills)
                .method(context.method())
                .params(context.params())
                .cache(context.target())
                ;

        //1. 获取刷新注解信息
        CacheInterceptor cacheInterceptor = context.interceptor();
        //根据注解信息执行before方法
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, true);

        //2. 正常执行
        Object result = context.process();

        final long endMills = System.currentTimeMillis();
        interceptorContext.endMills(endMills).result(result);

        // 方法执行完成
        //根据注解信息执行after方法
        this.interceptorHandler(cacheInterceptor, interceptorContext, cache, false);
        return result;
    }

    /**
     * 拦截器执行类
     * @param cacheInterceptor 缓存拦截器
     * @param interceptorContext 上下文
     * @param cache 缓存
     * @param before 是否执行执行
     */
    @SuppressWarnings("all")
    private void interceptorHandler(CacheInterceptor cacheInterceptor,
                                    CacheInterceptorContext interceptorContext,
                                    ICache cache,
                                    boolean before) {
        if(cacheInterceptor != null) {
            //1. 通用
            if(cacheInterceptor.common()) {
                for(ICacheInterceptor interceptor : commonInterceptors) {
                    if(before) {
                        interceptor.before(interceptorContext);
                    } else {
                        interceptor.after(interceptorContext);
                    }
                }
            }

            //2. 刷新
            if(cacheInterceptor.refresh()) {
                for(ICacheInterceptor interceptor : refreshInterceptors) {
                    if(before) {
                        interceptor.before(interceptorContext);
                    } else {
                        interceptor.after(interceptorContext);
                    }
                }
            }

            //3. AOF 追加 如果开启了aof注解并且是设置的是aof形式追加，则执行追加过程
            // 或者rdb aof追加
            final ICachePersist cachePersist = cache.persist();
            if(cacheInterceptor.aof() && (cachePersist instanceof CachePersistAof)) {
                if(before) {
                    persistInterceptors.before(interceptorContext);
                } else {
                    persistInterceptors.after(interceptorContext);
                }
            }

            final ICachePersist cacheRDBAOFPersist = cache.persist();
            if(cacheInterceptor.rdbaof() && (cachePersist instanceof CachePersistAofRdb)) {
                if(before) {
                    persistInterceptors2.before(interceptorContext);
                } else {
                    persistInterceptors2.after(interceptorContext);
                }
            }

            //4. 淘汰策略更新
            if(cacheInterceptor.evict()) {
                if(before) {
                    evictInterceptors.before(interceptorContext);
                } else {
                    evictInterceptors.after(interceptorContext);
                }
            }
        }
    }

}
