/*
 * Copyright (c)  2019. houbinbin Inc.
 * async All rights reserved.
 */

package com.myCache.support.proxy;

import com.myCache.api.ICache;
import com.myCache.support.proxy.cglib.CglibProxy;
import com.myCache.support.proxy.dynamic.DynamicProxy;
import com.myCache.support.proxy.none.NoneProxy;
import com.github.houbb.heaven.util.lang.ObjectUtil;

import java.lang.reflect.Proxy;

/**
 * <p> 代理信息 </p>
 *
 * <pre> Created: 2019/3/8 10:38 AM  </pre>
 * <pre> Project: async  </pre>
 *
 * @author houbinbin
 * 
 */
public final class CacheProxy {

    private CacheProxy(){}

    /**
     * 获取对象代理
     * @param <K> 泛型 key
     * @param <V> 泛型 value
     * @param cache 对象代理
     * @return 代理信息
     */
    @SuppressWarnings("all")
    public static <K,V> ICache<K,V> getProxy(final ICache<K,V> cache) {
        //尝试获取代理，如果传来的缓存对象为空的话，则通过NoneProxy返回null的代理，也就是null
        if(ObjectUtil.isNull(cache)) {
            return (ICache<K,V>) new NoneProxy(cache).proxy();
        }

        final Class clazz = cache.getClass();

        // 如果targetClass本身是个接口或者targetClass是JDK Proxy生成的,则使用JDK动态代理。
        // 动态代理是利用反射机制生成一个实现代理接口的匿名类，再调用具体方法之前调用InvokeHandler来处理
        // 而CGLIB动态代理是将代理对象类的class文件加载进来，通过修改器字节码生成子类来处理。
        // 参考 spring 的 AOP 判断
        if (clazz.isInterface() || Proxy.isProxyClass(clazz)) {
            return (ICache<K,V>) new DynamicProxy(cache).proxy();
        }
        return (ICache<K,V>) new CglibProxy(cache).proxy();
    }

}
