package com.myCache.support.evict;

import com.myCache.api.ICacheEntry;
import com.myCache.api.ICacheEvict;
import com.myCache.api.ICacheEvictContext;

/**
 * 丢弃策略-抽象实现类
 *  在这里使用抽象类的原因：
 *      抽象类具有更快的速度
 *      如果希望把一系列行为都规范在类继承层次中，并且可以更好地在同一个地方进行编码，那么选择抽象类
 * @author Jiangth
 */
public abstract class AbstractCacheEvict<K,V> implements ICacheEvict<K,V> {

    @Override
    public ICacheEntry<K,V> evict(ICacheEvictContext<K, V> context) {
        //3. 返回结果
        return doEvict(context);
    }

    /**
     * 执行移除
     * @param context 上下文
     * @return 结果
     * 1
     */
    protected abstract ICacheEntry<K,V> doEvict(ICacheEvictContext<K, V> context);

    @Override
    public void updateKey(K key) {

    }

    @Override
    public void removeKey(K key) {

    }
}
