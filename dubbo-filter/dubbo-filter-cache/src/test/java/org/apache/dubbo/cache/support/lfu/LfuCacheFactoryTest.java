package org.apache.dubbo.cache.support.lfu;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.cache.support.AbstractCacheFactory;
import org.apache.dubbo.cache.support.AbstractCacheFactoryTest;
import org.apache.dubbo.common.utils.LFUCache;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LfuCacheFactoryTest extends AbstractCacheFactoryTest {
    @Test
    public void testLfuCacheFactory() throws Exception {
        Cache cache = super.constructCache();
        assertThat(cache instanceof LfuCache, is(true));
    }

    @Override
    protected AbstractCacheFactory getCacheFactory() {
        return new LfuCacheFactory();
    }

    @Test
    public void lfuTest() {
        LFUCache cache = new LFUCache(2);
        cache.put(2, 1);
        cache.put(2, 3);
        assertThat(cache.get(2), is(3));    // returns 3
        cache.put(1, 1);
        cache.put(4, 4);    // evicts key 1.
        assertThat(cache.get(2), is(3));    // returns -1 (not found)
        assertThat(cache.get(4), is(4));    // returns 4
    }
}
