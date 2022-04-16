package com.myCache.support.persist;

import com.alibaba.fastjson.JSON;
import com.github.houbb.heaven.util.io.FileUtil;
import com.github.houbb.heaven.util.lang.StringUtil;
import com.github.houbb.log.integration.core.Log;
import com.github.houbb.log.integration.core.LogFactory;
import com.myCache.api.ICache;
import com.myCache.api.ICachePersist;
import com.myCache.model.PersistRdbEntry;

import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Jiangth
 */
public class CachePersistAofRdb<K,V> extends CachePersistAdaptor<K,V> {

    private static final Log log = LogFactory.getLog(CachePersistAof.class);

    /**
     * 缓存列表
     */
    private final List<String> bufferList = new ArrayList<>();

    /**
     * 数据持久化路径
     */
    private String dbPath;

    private int num=10;

    private int count=1;

    public CachePersistAofRdb(String dbPath,int num) {
        this.num=num;
        this.dbPath = dbPath;
    }

    @Override
    public void persist(ICache<K, V> cache) {
        String aofPath="AOF"+dbPath;
        //RDB 缓存
        if(count%num==0){
            String rdbPath="RDB"+dbPath;
            Set<Map.Entry<K,V>> entrySet = cache.entrySet();
            log.info("开始 RDB 持久化到文件");
            // 创建文件
            if(!FileUtil.exists(aofPath)) {
                FileUtil.createFile(aofPath);
            }
            // 清空文件
            FileUtil.truncate(rdbPath);
            //开始缓存
            for(Map.Entry<K,V> entry : entrySet) {
                K key = entry.getKey();
                Long expireTime = cache.expire().expireTime(key);
                PersistRdbEntry<K,V> persistRdbEntry = new PersistRdbEntry<>();
                persistRdbEntry.setKey(key);
                persistRdbEntry.setValue(entry.getValue());
                persistRdbEntry.setExpire(expireTime);

                String line = JSON.toJSONString(persistRdbEntry);
                FileUtil.write(rdbPath, line, StandardOpenOption.APPEND);
            }
            log.info("完成 RDB 持久化到文件");
            //删除AOF缓存
            log.info("清空 AOF 缓存文件");
            FileUtil.truncate(aofPath);
            bufferList.clear();
            log.info("清空 AOF 缓存文件完毕");
        }
        //AOF 缓存
        log.info("开始 AOF 持久化到文件");
        // 1. 创建文件
        if(!FileUtil.exists(aofPath)) {
            FileUtil.createFile(aofPath);
        }
        // 2. 持久化追加到文件中
        FileUtil.append(aofPath, bufferList);
        // 3. 清空 buffer 列表
        bufferList.clear();
        log.info("完成 AOF 持久化到文件");
        count++;
    }

    public void append(final String json) {
        if(StringUtil.isNotEmpty(json)) {
            bufferList.add(json);
        }
    }

    @Override
    public long delay() {
        return 1;
    }

    @Override
    public long period() {
        return 1;
    }

    @Override
    public TimeUnit timeUnit() {
        return TimeUnit.SECONDS;
    }
}
