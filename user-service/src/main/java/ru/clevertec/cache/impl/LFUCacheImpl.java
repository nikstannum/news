package ru.clevertec.cache.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.CacheElm;

@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "LFU")
public class LFUCacheImpl implements Cache {

    private final Map<String, Object> map;
    private final TreeMap<CacheElm, String> sortedMap;
    @Value("${spring.cache.size}")
    private int cacheSize;
    @Value("${spring.cache.time-clear}")
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

    @Override
    public boolean contains(Object id, Object target) {
        String compositeId = id + ":" + target;
        return map.containsKey(compositeId);
    }

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
