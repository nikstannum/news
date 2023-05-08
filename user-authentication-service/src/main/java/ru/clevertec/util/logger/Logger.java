package ru.clevertec.util.logger;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class Logger {
    /**
     * логирование методов на уровне ИНФО вне слоя контроллеров при успешном их выполнении
     *
     * @param jp
     */

    @AfterReturning("@annotation(LogInvocation) && !within(ru.clevertec.web..*)")
    private void loggingInfo(JoinPoint jp) {
        log.info("Method " + jp.getSignature().getName() + " with args " + Arrays.toString(jp.getArgs())
                + " on the object " + jp.getTarget() + " was called successfully.");
    }

    /**
     * логирование ошибок во всем приложении
     * @param e
     */
    @AfterThrowing(pointcut = "@annotation(LogInvocation) && within(ru.clevertec.client.FeignErrorDecoder)", throwing = "e")
    private void loggingError(Exception e) {
        log.error(String.valueOf(e));
    }


    @Pointcut("execution(* ru.clevertec..Cache.take(..))")
    public void takeFromCache() {
    }

    /**
     * логирование факта получения объекта из кэша. Поинткат определен в takeFromCache().
     * @param returnValue
     */
    @AfterReturning(value = "takeFromCache()", returning = "returnValue")
    private void loggingTakeFromCache(Object returnValue) {
        if (returnValue != null) {
            log.info("taken from cache " + returnValue);
        } else {
            log.error("cache returned null value");
        }
    }

    @Pointcut("execution(* ru.clevertec..Cache.put(..))")
    public void putIntoCache() {
    }

    /**
     * логирование факта помещения объекта в кэш. Поинткат определен в putIntoCache()
     * @param jp
     */
    @AfterReturning(value = "putIntoCache()")
    private void loggingPutInCache(JoinPoint jp) {
        Object value = jp.getArgs()[2];
        if (value != null) {
            log.info("put in cache " + value);
        } else {
            log.error("cache set to null");
        }
    }

    @Pointcut("execution(* ru.clevertec..Cache.delete(..))")
    public void deleteFromCache() {
    }

    /**
     * логирование факта удаления объекта из кэша. Поинткат определен в deleteFromCache()
     * @param returnValue
     */
    @AfterReturning(value = "deleteFromCache()", returning = "returnValue")
    private void loggingDeleteFromCache(Object returnValue) {
        if (returnValue != null) {
            log.info("delete from cache " + returnValue);
        } else {
            log.error("the cache contained a null value");
        }
    }

    @Pointcut("within(ru.clevertec.web.RestUserController)")
    public void request() {
    }

    /**
     * логирование запросов к контроллерам. Поинткат определен в request()
     * @param jp
     */
    @Before("request()")
    private void loggingRequest(JoinPoint jp) {
        log.info("request to " + jp.getTarget().getClass() +
                ", method " + jp.getSignature().getName() +
                ", with args " + Arrays.toString(jp.getArgs()));
    }

    @Pointcut("within(ru.clevertec.web.RestUserController)")
    public void response() {
    }

    /**
     * логирование ответов от контроллера. Поинткат определен в response()
     * @param jp
     * @param returnValue
     */
    @AfterReturning(value = "response()", returning = "returnValue")
    private void loggingResponse(JoinPoint jp, Object returnValue) {
        log.info("response from " + jp.getTarget().getClass() +
                ", method " + jp.getSignature().getName() +
                ", with returned value " + returnValue);
    }

    @Pointcut("within(ru.clevertec.web.exc_handler.RestExceptionAdvice)")
    public void excAdvice() {
    }

    /**
     * логирование перехваченных исключений в RestExceptionAdvice. Поинткат определен в excAdvice()
     */
    @AfterReturning(value = "excAdvice()")
    private void loggingAdvice(JoinPoint jp) {
        Exception e = (Exception) jp.getArgs()[0];
        log.error(Arrays.toString(e.getStackTrace()));
    }
}
