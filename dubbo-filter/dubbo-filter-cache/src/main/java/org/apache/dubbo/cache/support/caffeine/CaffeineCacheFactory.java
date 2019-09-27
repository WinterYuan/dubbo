package org.apache.dubbo.cache.support.caffeine;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.cache.support.AbstractCacheFactory;
import org.apache.dubbo.common.URL;

public class CaffeineCacheFactory extends AbstractCacheFactory {
    @Override
    protected Cache createCache(URL url) {
        return new CaffeineCache(url);
    }
}
