package com.myCache.api;

/**
 * 删除监听器接口
 * @author Jiangth
 */
public interface ICacheRemoveListener<K,V> {

    /**
     * 监听
     * @param context 上下文
     */
    void listen(final ICacheRemoveListenerContext<K,V> context);

}
