import com.myCache.api.ICache;
import com.myCache.bs.CacheBs;
import com.myCache.support.evict.CacheEvictLru;
import com.myCache.support.evict.CacheEvicts;
import com.myCache.support.load.CacheLoads;
import com.myCache.support.persist.CachePersists;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author Jiangth
 */
public class CacheBsTest {
    @Test
    public void helloTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

    //测试先进先出
    @Test
    public void configTest() {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .evict(CacheEvicts.<String, String>fifo())
                .size(2)
                .build();

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");

        Assert.assertEquals(2, cache.size());
        System.out.println(cache.keySet());
    }

    //测试lru 发现问题
    @Test
    public void test1() throws InterruptedException{
        ICache<String,String> cache=CacheBs.<String,String>newInstance()
                .size(2).evict(CacheEvicts.<String,String>lru()).build();
        cache.put("1","1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.get("2");
        System.out.println(cache.keySet());
    }

    //测试lfu
    @Test
    public void lfuTest()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lfu())
                .build();

        cache.put("A", "hello");
        cache.put("B", "world");
        cache.put("C", "FIFO");

        // 访问一次A
        cache.get("A");
        cache.get("B");
        cache.remove("C");
        cache.put("D", "LRU");
        cache.put("E", "LRU");

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }

    @Test
    public void lruQ2Test()  {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .evict(CacheEvicts.<String, String>lru2Q())
                .build();

        cache.put("A", "hello");
        cache.get("A");
        cache.put("B", "world");
        cache.get("B");
        cache.put("C", "FIFO");
        cache.get("C");
        //加D删A
        cache.put("D", "LRU");
        cache.get("D");
        //加E删B
        cache.put("E", "LRU");

        Assert.assertEquals(3, cache.size());
        //剩下CDE
        System.out.println(cache.keySet());
    }


    @Test
    public void persistAofTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .size(3)
                .persist(CachePersists.<String, String>rdbaof("persist",5))
//                .persist(CachePersists.<String, String>aof("persist"))
                .build();

        cache.put("1", "1");
        cache.put("2","2");
        TimeUnit.SECONDS.sleep(10);
        cache.put("3", "3");
        Assert.assertEquals(3, cache.size());
        TimeUnit.SECONDS.sleep(2);
    }


    @Test
    public void persistRdbTest() throws InterruptedException {
        ICache<String, String> cache = CacheBs.<String,String>newInstance()
                .load(CacheLoads.<String, String>rdbaof("persist"))
                .build();

        Assert.assertEquals(3, cache.size());
        System.out.println(cache.keySet());
    }



}
