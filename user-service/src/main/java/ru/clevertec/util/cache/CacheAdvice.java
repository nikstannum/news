package ru.clevertec.util.cache;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ru.clevertec.service.dto.ClientUserReadDto;

/**
 * Class for applying end-to-end functionality when working with the cache.
 */
@Aspect
@RequiredArgsConstructor
public class CacheAdvice {
    private final Cache cache;

    /**
     * The advice to apply when trying to get an object. If the object is not in the cache, the application continues to work to get the object
     * from a non-public microservice (user-data-service). After receiving the desired object, it is placed in the cache.
     *
     * @param jp join point for advice
     * @return Object placed in the cache ({@link ru.clevertec.service.dto.ClientUserReadDto})
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CacheGet)")
    private Object get(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.getTarget();
        Object id = jp.getArgs()[0];
        if (cache.contains(id, target)) {
            return cache.take(id, target);
        } else {
            Object value = jp.proceed();
            cache.put(id, target, value);
            return value;
        }
    }

    /**
     * The advice to apply when creating or updating an object. The created or updated object is passed to a non-public microservice
     * (data-user-service) for saving or updating, after which this object is placed or updated in the cache.
     *
     * @param jp join point for advice
     * @return created or updated object ({@link ru.clevertec.service.dto.ClientUserReadDto})
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CachePutPost)")
    private Object post(ProceedingJoinPoint jp) throws Throwable {
        Object value = jp.proceed();
        Object target = jp.getTarget();
        ClientUserReadDto user = (ClientUserReadDto) value;
        cache.put(user.getId(), target, value);
        return value;
    }

    /**
     * The advice to apply when deleting an object. When deleting, the identifier of the object being deleted is passed to a non-public microservice
     * (user-data-service) for deletion. After a deletion is performed, the object is removed from the cache.
     *
     * @param jp join point for advice
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CacheDelete)")
    private void delete(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.getTarget();
        Object id = jp.getArgs()[0];
        cache.delete(id, target);
        jp.proceed();
    }
}
