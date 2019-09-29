package org.apache.dubbo.cache.support.lfu;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.cache.support.AbstractCacheFactory;
import org.apache.dubbo.common.URL;

public class LfuCacheFactory extends AbstractCacheFactory {
    @Override
    protected Cache createCache(URL url) {
        return new LfuCache(url);
    }
}
