package ru.clevertec.util.cache;

public interface Cache {
    void put(Object id, Object target, Object value);

    Object delete(Object id, Object target);

    boolean contains(Object id, Object target);

    Object take(Object id, Object target);
}
