package ru.clevertec.service.cache;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.clevertec.client.User;

@Component
@Aspect
@RequiredArgsConstructor
public class CacheCheckingAdvice {
    private final Cache cache;

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

    @Around("@annotation(CachePutPost)")
    private Object post(ProceedingJoinPoint jp) throws Throwable {
        Object value = jp.proceed();
        Object target = jp.getTarget();
        User user = (User) value;
        cache.put(user.getId(), target, value);
        return value;
    }

    @Around("@annotation(CacheDelete)")
    private boolean delete(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.getTarget();
        Object id = jp.getArgs()[0];
        if (jp.proceed().equals(true)) {
            return cache.delete(id, target);
        }
        return false;
    }
}
