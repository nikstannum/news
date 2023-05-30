package ru.clevertec.util.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.util.cache.Cache;
import ru.clevertec.util.cache.CacheElm;

/**
 * Implementation of the LFU Cache. When performing operations with the cache, the time spent by objects in the cache is checked.
 * If the time spent by objects exceeds the set value, then these objects are removed from the cache.
 */
public class LFUCacheImpl implements Cache {

    private final Map<String, Object> map;
    private final Map<String, Timer> timers;
    private final NavigableMap<CacheElm, String> sortedMap;
    @Value("${app.cache.size}")
    private int cacheSize;
    @Value("${app.cache.time-to-live}")
    private Integer expirationTime;

    public LFUCacheImpl() {
        this.timers = new HashMap<>(cacheSize);
        this.map = new ConcurrentHashMap<>(cacheSize);
        this.sortedMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            Integer quantity1 = o1.getQuantityUse();
            Integer quantity2 = o2.getQuantityUse();
            if (!quantity1.equals(quantity2)) {
                return quantity1.compareTo(quantity2);
            }
            return o1.toString().compareTo(o2.toString());
        });
    }

    /**
     * Method for placing an object in the cache.
     *
     * @param key       computed based on expression language
     * @param cacheName the name given to the cached object. Used when compiling a composite key to access a cached object
     * @param value     the object itself
     */
    @Override
    public void put(String key, String cacheName, Object value) {
        String compositeId = key + ":" + cacheName;
        if (map.containsKey(compositeId)) {
            setUpdatedCacheInf(compositeId, value);
            return;
        }
        if (cacheSize == map.size()) {
            removeElm();
        }
        putNewCacheElm(compositeId, value);
    }

    private void scheduleRemoval(String compositeId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CacheElm elm = (CacheElm) map.remove(compositeId);
                sortedMap.remove(elm);
                timers.remove(compositeId);
                timer.cancel();
            }
        }, expirationTime * 1000);
        timers.put(compositeId, timer);
    }

    private void setUpdatedCacheInf(String compositeId, Object value) {
        deleteTimer(compositeId);
        CacheElm cacheElm = (CacheElm) map.get(compositeId);
        cacheElm.setQuantityUse(cacheElm.getQuantityUse() + 1);
        cacheElm.setValue(value);
        scheduleRemoval(compositeId);
    }

    /**
     * Method for removing an object from the cache.
     *
     * @param key       computed based on expression language
     * @param cacheName the name given to the cached object. Used when compiling a composite key to access a cached object
     */
    @Override
    public void delete(String key, String cacheName) {
        String compositeId = key + ":" + cacheName;
        if (map.containsKey(compositeId)) {
            deleteTimer(compositeId);
            Object obj = map.remove(compositeId);
            CacheElm cacheElm = (CacheElm) obj;
            sortedMap.remove(cacheElm);
        }
    }

    private void deleteTimer(String compositeId) {
        Timer timer = timers.get(compositeId);
        if (timer != null) {
            timer.cancel();
            timers.remove(compositeId);
        }
    }


    /**
     * Method for getting an object from the cache.
     *
     * @param key       computed based on expression language
     * @param cacheName the name given to the cached object. Used when compiling a composite key to access a cached object
     * @return the object itself
     */
    @Override
    public Object take(String key, String cacheName) {
        String compositeId = key + ":" + cacheName;
        CacheElm cacheElm = (CacheElm) map.get(compositeId);
        Object obj = cacheElm.getValue();
        setUpdatedCacheInf(compositeId, obj);
        return obj;
    }

    private void removeElm() {
        CacheElm firstKeyForRemove = sortedMap.firstKey();
        String compositeId = sortedMap.remove(firstKeyForRemove);
        map.remove(compositeId);
        deleteTimer(compositeId);
    }

    private void putNewCacheElm(String compositeId, Object value) {
        CacheElm cacheElm = new CacheElm();
        cacheElm.setQuantityUse(1);
        cacheElm.setValue(value);
        map.put(compositeId, cacheElm);
        sortedMap.put(cacheElm, compositeId);
        scheduleRemoval(compositeId);
    }
}
