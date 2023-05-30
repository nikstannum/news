package ru.clevertec.util.cache.impl;

import java.util.Deque;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.util.cache.Cache;

/**
 * Implementation of the LRU Cache.
 */
public class LRUCacheImpl implements Cache {

    private final Map<String, Object> map;
    private final Deque<String> keyList;
    private final Map<String, Timer> timers;
    @Value("${app.cache.size}")
    private int cacheSize;
    @Value("${app.cache.time-to-live}")
    private Integer expirationTime;

    public LRUCacheImpl() {
        this.timers = new ConcurrentHashMap<>(cacheSize);
        this.map = new ConcurrentHashMap<>();
        this.keyList = new ConcurrentLinkedDeque<>();
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
            moveToFirst(compositeId, value);
            return;
        }
        if (keyList.size() == cacheSize) {
            String forRemove = keyList.removeLast();
            map.remove(forRemove);
        }
        keyList.addFirst(compositeId);
        map.put(compositeId, value);
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
            keyList.remove(compositeId);
            map.remove(compositeId);
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
        Object value = map.get(compositeId);
        moveToFirst(compositeId, value);
        return value;
    }

    private void moveToFirst(String compositeId, Object value) {
        deleteTimer(compositeId);
        keyList.remove(compositeId);
        keyList.addFirst(compositeId);
        map.put(compositeId, value);
        scheduleRemoval(compositeId);
    }

    private void deleteTimer(String compositeId) {
        Timer timer = timers.get(compositeId);
        if (timer != null) {
            timer.cancel();
            timers.remove(compositeId);
        }
    }

    private void scheduleRemoval(String compositeId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                map.remove(compositeId);
                timers.remove(compositeId);
                timer.cancel();
            }
        }, expirationTime * 1000);
        timers.put(compositeId, timer);
    }
}
