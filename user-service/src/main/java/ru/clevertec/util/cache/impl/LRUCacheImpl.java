package ru.clevertec.util.cache.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import ru.clevertec.util.cache.Cache;

/**
 * Implementation of the LRU Cache.
 */
public class LRUCacheImpl implements Cache {

    private final Map<String, Object> map;
    private final LinkedList<String> keyList;
    @Value("${app.cache.size}")
    private int size;

    public LRUCacheImpl() {
        this.map = new HashMap<>();
        this.keyList = new LinkedList<>();
    }

    /**
     * Method for placing an object in the cache.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientUserReadDto}) unique identifier
     * @param target the object on which the method is called
     * @param value  the object itself ({@link ru.clevertec.service.dto.ClientUserReadDto})
     */
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

    /**
     * Method for removing an object from the cache. The return value is used to log the fact that the object was removed from the cache.
     * If a null value has entered the cache, the fact of deletion is logged at the error level.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientUserReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return object removed from the cache
     */
    @Override
    public Object delete(Object id, Object target) {
        if (contains(id, target)) {
            String compositeId = id + ":" + target;
            keyList.remove(compositeId);
            return map.remove(compositeId);
        }
        return null;
    }

    /**
     * Method for checking if an object is in the cache.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientUserReadDto}) unique identifier
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
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientUserReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return the object itself ({@link ru.clevertec.service.dto.ClientUserReadDto})
     */
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
