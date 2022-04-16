package com.myCache.support.load;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.ObjectUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.heaven.util.lang.reflect.ReflectMethodUtil;
import com.github.houbb.heaven.util.util.CollectionUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.myCache.annotation.CacheInterceptor;
import com.myCache.api.ICache;
import com.myCache.api.ICacheLoad;
import com.myCache.core.Cache;
import com.myCache.model.PersistAofEntry;
import com.myCache.model.PersistRdbEntry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载策略，AOF_RDB
 * @author Jiangth
 */
public class CacheLoadAofRdb<K,V> implements ICacheLoad<K,V> {

    private static final Log log = LogFactory.getLog(CacheLoadAof.class);

    private static final Map<String, Method> METHOD_MAP = new HashMap<>();

    static {
        Method[] methods = Cache.class.getMethods();

        for(Method method : methods){
            CacheInterceptor cacheInterceptor = method.getAnnotation(CacheInterceptor.class);

            if(cacheInterceptor != null) {
                // 暂时
                if(cacheInterceptor.aof()) {
                    String methodName = method.getName();

                    METHOD_MAP.put(methodName, method);
                }
            }
        }

    }

    /**
     * 文件路径
     *
     */
    private final String aofPath;
    private final String rdbPath;

    public CacheLoadAofRdb(String dbPath) {
        this.aofPath = "AOF"+dbPath;
        this.rdbPath = "RDB"+dbPath;
    }

    @Override
    public void load(ICache<K, V> cache) {
        List<String> lines = FileUtil.readAllLines(aofPath);
        log.info("[load] 开始处理 path: {}", aofPath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接跳过", aofPath);
        }else{
            for(String line : lines) {
                if(StringUtil.isEmpty(line)) {
                    continue;
                }
                // 执行
                // 简单的类型还行，复杂的这种反序列化会失败
                PersistAofEntry entry = JSON.parseObject(line, PersistAofEntry.class);
                final String methodName = entry.getMethodName();
                final Object[] objects = entry.getParams();
                final Method method = METHOD_MAP.get(methodName);
                // 反射调用
                ReflectMethodUtil.invoke(cache, method, objects);
            }
        }
        log.info("[load] 开始处理 path: {}", rdbPath);
        lines = FileUtil.readAllLines(rdbPath);
        log.info("[load] 开始处理 path: {}", rdbPath);
        if(CollectionUtil.isEmpty(lines)) {
            log.info("[load] path: {} 文件内容为空，直接跳过", rdbPath);
        }else {
            for(String line : lines) {
                if(StringUtil.isEmpty(line)) {
                    continue;
                }

                // 执行
                // 简单的类型还行，复杂的这种反序列化会失败
                PersistRdbEntry<K,V> entry = JSON.parseObject(line, PersistRdbEntry.class);

                K key = entry.getKey();
                V value = entry.getValue();
                Long expire = entry.getExpire();

                cache.put(key, value);
                if(ObjectUtil.isNotNull(expire)) {
                    cache.expireAt(key, expire);
                }
            }
        }
    }
}
