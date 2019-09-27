package org.apache.dubbo.cache.support.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.common.URL;

import java.util.concurrent.TimeUnit;

public class CaffeineCache implements Cache {

    com.github.benmanes.caffeine.cache.Cache <Object, Object> store;

    public CaffeineCache(URL url) {
        store = Caffeine.newBuilder()
                .initialCapacity(32)
                .maximumSize(1000)
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void put(Object key, Object value) {
        store.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return store.getIfPresent(key);
    }
}
