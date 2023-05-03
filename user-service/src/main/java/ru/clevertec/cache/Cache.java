package ru.clevertec.cache;

import ru.clevertec.logger.LogInvocation;

public interface Cache {
    @LogInvocation
    void put(Object id, Object target, Object value);

    @LogInvocation
    Object delete(Object id, Object target);

    boolean contains(Object id, Object target);

    @LogInvocation
    Object take(Object id, Object target);
}
