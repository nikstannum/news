package ru.clevertec.util.cache;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

/**
 * Class for applying end-to-end functionality when working with the cache.
 */
@Aspect
@RequiredArgsConstructor
public class CacheAdvice {
    private final Cache cache;
    private final ExpressionParser parser;
    private final EvaluationContext context;
    private final Lock lock = new ReentrantLock(true);

    /**
     * The advice to apply when trying to get an object. If the object is not in the cache, the application continues to work to get the object
     * from a non-public microservice. After receiving the desired object, it is placed in the cache.
     *
     * @param jp join point for advice
     * @return Object placed in the cache
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CacheGet)")
    private Object get(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        CacheGet annotation = method.getAnnotation(CacheGet.class);
        String cacheName = annotation.cacheName();
        String key = generateKey(annotation.key(), jp);
        lock.lock();
        Object object = cache.take(key, cacheName);
        if (Objects.nonNull(object)) {
            lock.unlock();
            return object;
        } else {
            Object value = jp.proceed();
            cache.put(key, cacheName, value);
            lock.unlock();
            return value;
        }
    }

    private String generateKey(String keyExpression, ProceedingJoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        String[] paramsName = signature.getParameterNames();
        Object[] args = jp.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramsName[i], args[i]);
        }
        Expression expression = parser.parseExpression(keyExpression);
        return expression.getValue(context, String.class);
    }

    /**
     * The advice to apply when creating or updating an object. The created or updated object is passed to a non-public microservice
     * for saving or updating, after which this object is placed or updated in the cache.
     *
     * @param jp join point for advice
     * @return created or updated object
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CachePutPost)")
    private Object post(ProceedingJoinPoint jp) throws Throwable {
        Object value = jp.proceed();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        CachePutPost annotation = method.getAnnotation(CachePutPost.class);
        String key = annotation.key();
        String cacheName = annotation.cacheName();
        cache.put(key, cacheName, value);
        return value;
    }

    /**
     * The advice to apply when deleting an object. When deleting, the identifier of the object being deleted is passed to a non-public
     * microservice for deletion. After a deletion is performed, the object is removed from the cache.
     *
     * @param jp join point for advice
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(CacheDelete)")
    private void delete(ProceedingJoinPoint jp) throws Throwable {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        CacheDelete annotation = method.getAnnotation(CacheDelete.class);
        String key = annotation.key();
        String cacheName = annotation.cacheName();
        cache.delete(key, cacheName);
        jp.proceed();
    }
}
