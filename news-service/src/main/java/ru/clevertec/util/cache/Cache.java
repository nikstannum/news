package ru.clevertec.util.cache;

/**
 * Application cache interface. Implementation classes: {@link ru.clevertec.util.cache.impl.LRUCacheImpl},
 * {@link ru.clevertec.util.cache.impl.LFUCacheImpl}
 */
public interface Cache {
    /**
     * Method for placing an object in the cache.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     *               unique identifier
     * @param target the object on which the method is called
     * @param value  the object itself ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     */
    void put(Object id, Object target, Object value);

    /**
     * Method for removing an object from the cache. The return value is used to log the fact that the object was removed from the cache.
     * If a null value has entered the cache, the fact of deletion is logged at the error level.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return object removed from the cache ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     */
    Object delete(Object id, Object target);

    /**
     * Method for checking if an object is in the cache.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return true if the object being looked up is in the cache, otherwise false
     */
    boolean contains(Object id, Object target);

    /**
     * Method for getting an object from the cache. Before getting an object, the fact of its presence in the cache is checked.
     *
     * @param id     object ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto}) unique identifier
     * @param target the object on which the method is called
     * @return the object itself ({@link ru.clevertec.service.dto.ClientNewsReadDto} or {@link ru.clevertec.service.dto.ClientCommentReadDto})
     */
    Object take(Object id, Object target);
}
