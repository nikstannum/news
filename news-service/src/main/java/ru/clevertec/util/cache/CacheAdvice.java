package ru.clevertec.util.cache;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.clevertec.service.dto.CommentReadDto;
import ru.clevertec.service.dto.NewsReadDto;

@Component
@Aspect
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "NONE")
public class CacheAdvice {
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
        String simpleClassName = value.getClass().getSimpleName();
        Object target = jp.getTarget();
        switch (simpleClassName) {
            case "NewsReadDto" -> {
                NewsReadDto newsReadDto = (NewsReadDto) value;
                cache.put(newsReadDto.getId(), target, value);
            }
            case "CommentReadDto" -> {
                CommentReadDto commentReadDto = (CommentReadDto) value;
                cache.put(commentReadDto.getId(), target, value);
            }
        }
        return value;
    }

    @Around("@annotation(CacheDelete)")
    private void delete(ProceedingJoinPoint jp) throws Throwable {
        Object target = jp.getTarget();
        Object id = jp.getArgs()[0];
        cache.delete(id, target);
        jp.proceed();
    }
}
