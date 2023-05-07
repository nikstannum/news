package ru.clevertec.util.cache;

import ru.clevertec.util.logger.LogInvocation;

public interface Cache {
    void put(Object id, Object target, Object value);

    Object delete(Object id, Object target);

    boolean contains(Object id, Object target);

    Object take(Object id, Object target);
}
