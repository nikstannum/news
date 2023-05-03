package ru.clevertec.cache.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.Cache;

@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "LRU")
public class LRUCacheImpl implements Cache {

    private final Map<String, Object> map;
    @Value("${spring.cache.size}")
    private int size;
    private final LinkedList<String> keyList;

    public LRUCacheImpl() {
        this.map = new HashMap<>();
        this.keyList = new LinkedList<>();
    }

    @Override
    public void put(Object id, Object target, Object value) {
        String compositeId = id + ":" + target;
        if (contains(id, target)) {
            moveToFirst(compositeId, value);
            return;
        }
        if (keyList.size() == size) {
            String forRemove = keyList.removeLast();
            map.remove(forRemove);
        }
        keyList.addFirst(compositeId);
        map.put(compositeId, value);
    }

    @Override
    public boolean delete(Object id, Object target) {
        if (contains(id, target)) {
            String compositeId = id + ":" + target;
            keyList.remove(compositeId);
            map.remove(compositeId);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object id, Object target) {
        String compositeId = id + ":" + target;
        return map.containsKey(compositeId);
    }

    @Override
    public Object take(Object id, Object target) {
        String compositeId = id + ":" + target;
        Object value = map.get(compositeId);
        moveToFirst(compositeId, value);
        return value;
    }

    private void moveToFirst(String compositeId, Object value) {
        keyList.remove(compositeId);
        keyList.addFirst(compositeId);
        map.put(compositeId, value);
    }
}
