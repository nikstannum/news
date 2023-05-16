package ru.clevertec.util.cache.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.util.cache.Cache;
import ru.clevertec.util.cache.CacheElm;

/**
 * Implementation of the LFU Cache. When performing operations with the cache, the time spent by objects in the cache is checked. If the time spent by objects exceeds the set value, then these objects are removed from the cache.
 */
public class LFUCacheImpl implements Cache {

    private final Map<String, Object> map;
    private final TreeMap<CacheElm, String> sortedMap;
    @Value("${app.cache.size}")
    private int cacheSize;
    @Value("${app.cache.time-to-live}")
    private Integer clearTime;

    public LFUCacheImpl() {
        this.map = new HashMap<>();
        this.sortedMap = new TreeMap<>((o1, o2) -> {
            Integer quantity1 = o1.getQuantityUse();
            Integer quantity2 = o2.getQuantityUse();
            if (!quantity1.equals(quantity2)) {
                return quantity1.compareTo(quantity2);
            }
            LocalDateTime time1 = o1.getLastTimeUse();
            LocalDateTime time2 = o2.getLastTimeUse();
            if (!time1.equals(time2)) {
                return time1.compareTo(time2);
            }
            return o1.toString().compareTo(o2.toString());
        });
    }

    /**
     * Method for placing an object in the cache.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     *               unique identifier
     * @param target the object on which the method is called
     * @param value  the object itself ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     */
    @Override
    public void put(Object id, Object target, Object value) {
        deleteUnusedLongTime(map, sortedMap);
        String compositeId = id + ":" + target;
        if (contains(id, target)) {
            setUpdatedCacheInf(compositeId, value);
            return;
        }
        if (cacheSize == map.size()) {
            removeElm();
        }
        putNewCacheElm(value, compositeId);
    }

    /**
     * Method for removing an object from the cache. The return value is used to log the fact that the object was removed from the cache.
     * If a null value has entered the cache, the fact of deletion is logged at the error level.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return object removed from the cache
     */
    @Override
    public Object delete(Object id, Object target) {
        deleteUnusedLongTime(map, sortedMap);
        if (contains(id, target)) {
            String compositeId = id + ":" + target;
            Object obj = map.remove(compositeId);
            CacheElm cacheElm = (CacheElm) obj;
            return sortedMap.remove(cacheElm);
        }
        return null;
    }

    /**
     * Method for checking if an object is in the cache.
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return true if the object being looked up is in the cache, otherwise false
     */
    @Override
    public boolean contains(Object id, Object target) {
        String compositeId = id + ":" + target;
        return map.containsKey(compositeId);
    }

    /**
     * Method for getting an object from the cache. Before getting an object, the fact of its presence in the cache is checked.
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return the object itself ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     */
    @Override
    public Object take(Object id, Object target) {
        String compositeId = id + ":" + target;
        CacheElm cacheElm = (CacheElm) map.get(compositeId);
        Object obj = cacheElm.getValue();
        setUpdatedCacheInf(compositeId, obj);
        deleteUnusedLongTime(map, sortedMap);
        return obj;
    }

    private void removeElm() {
        CacheElm firstKeyForRemove = sortedMap.firstKey();
        String removed = sortedMap.remove(firstKeyForRemove);
        map.remove(removed);
    }

    private boolean isTimeForDeleting(CacheElm inf) {
        if (clearTime == null) {
            return false;
        }
        LocalDateTime lastTimeUse = inf.getLastTimeUse();
        Duration realDurationStorage = Duration.between(lastTimeUse, LocalDateTime.now());
        long secondsInStorage = realDurationStorage.getSeconds();
        return secondsInStorage >= clearTime;
    }

    private void putNewCacheElm(Object value, String compositeId) {
        CacheElm cacheElm = new CacheElm();
        cacheElm.setQuantityUse(1);
        cacheElm.setLastTimeUse(LocalDateTime.now());
        cacheElm.setValue(value);
        map.put(compositeId, cacheElm);
        sortedMap.put(cacheElm, compositeId);
    }

    private void deleteUnusedLongTime(Map<String, Object> map, SortedMap<CacheElm, String> sortedMap) {
        List<CacheElm> listForDelete = sortedMap.keySet().stream()
                .filter(this::isTimeForDeleting).toList();
        listForDelete.forEach((key) -> {
            String keyForMap = sortedMap.remove(key);
            map.remove(keyForMap);
        });
    }

    private void setUpdatedCacheInf(String compositeId, Object value) {
        CacheElm cacheElm = (CacheElm) map.get(compositeId);
        cacheElm.setLastTimeUse(LocalDateTime.now());
        cacheElm.setQuantityUse(cacheElm.getQuantityUse() + 1);
        cacheElm.setValue(value);
    }
}
