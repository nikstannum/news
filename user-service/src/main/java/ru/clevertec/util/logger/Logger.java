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

/**
 * Class for applying end-to-end functionality for logging method calls.
 */
@Component
@Aspect
@Slf4j
public class Logger {
    /**
     * Logging methods at the INFO level outside the controller layer when they are successfully executed
     *
     * @param jp join point for advice
     */

    @AfterReturning("@annotation(LogInvocation) && !within(ru.clevertec.web..*)")
    private void loggingInfo(JoinPoint jp) {
        log.info("Method " + jp.getSignature().getName() + " with args " + Arrays.toString(jp.getArgs())
                + " on the object " + jp.getTarget() + " was called successfully.");
    }

    /**
     * Exceptions logging.
     *
     * @param e thrown exception
     */
    @AfterThrowing(pointcut = "@annotation(LogInvocation) && within(ru.clevertec.client.FeignErrorDecoder)", throwing = "e")
    private void loggingError(Exception e) {
        log.error(String.valueOf(e));
    }


    @Pointcut("execution(* ru.clevertec..Cache.take(..))")
    public void takeFromCache() {
    }

    /**
     * Logging the fact of getting an object from the cache. The pointcut is defined in takeFromCache().
     *
     * @param returnValue object retrieved from cache
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
     * Logging the fact of placing an object in the cache. Pointcut is defined in putIntoCache()
     *
     * @param jp join point for advice
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
     * Logging the fact of deleting an object from the cache. The pointcut is defined in deleteFromCache()
     *
     * @param returnValue object removed from the cache
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
     * Logging requests to the controller. Pointcut is defined in request()
     *
     * @param jp join point for advice
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
     * Logging responses from the controller. Pointcut defined in response()
     *
     * @param jp          join point for advice
     * @param returnValue object returned from controller method
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
     * Logging of caught exceptions to RestExceptionAdvice. Pointcut is defined in excAdvice()
     */
    @AfterReturning(value = "excAdvice()")
    private void loggingAdvice(JoinPoint jp) {
        Exception e = (Exception) jp.getArgs()[0];
        log.error(Arrays.toString(e.getStackTrace()));
    }
}
