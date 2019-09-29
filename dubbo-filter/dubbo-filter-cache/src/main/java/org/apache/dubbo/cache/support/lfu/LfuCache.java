package org.apache.dubbo.cache.support.lfu;

import org.apache.dubbo.cache.Cache;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.LFUCache;

public class LfuCache implements Cache {

    /**
     * This is used to store cache records
     */
    private final LFUCache store;

    /**
     * Initialize LruCache, it uses constructor argument <b>cache.size</b> value as its storage max size.
     *  If nothing is provided then it will use 1000 as default value.
     * @param url A valid URL instance
     */
    public LfuCache(URL url) {
        final int max = url.getParameter("cache.size", 1000);
        this.store = new LFUCache(max);
    }

    /**
     * API to store value against a key in the calling thread scope.
     * @param key  Unique identifier for the object being store.
     * @param value Value getting store
     */
    @Override
    public void put(Object key, Object value) {
        store.put(key, value);
    }

    /**
     * API to return stored value using a key against the calling thread specific store.
     * @param key Unique identifier for cache lookup
     * @return Return stored object against key
     */
    @Override
    public Object get(Object key) {
        return store.get(key);
    }
}

