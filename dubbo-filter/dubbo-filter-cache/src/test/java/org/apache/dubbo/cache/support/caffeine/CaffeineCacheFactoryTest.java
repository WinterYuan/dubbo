package org.apache.dubbo.cache.support.caffeine;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.cache.support.AbstractCacheFactory;
import org.apache.dubbo.cache.support.AbstractCacheFactoryTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CaffeineCacheFactoryTest extends AbstractCacheFactoryTest {
    @Test
    public void testCaffeineCacheFactory() throws Exception {
        Cache cache = super.constructCache();
        assertThat(cache instanceof CaffeineCache, is(true));
    }

    @Override
    protected AbstractCacheFactory getCacheFactory() {
        return new CaffeineCacheFactory();
    }
}
