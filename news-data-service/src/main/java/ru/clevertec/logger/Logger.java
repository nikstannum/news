package ru.clevertec.logger;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class Logger {

    @AfterReturning("@annotation(LogInvocation)")
    private void loggingInfo(JoinPoint jp) {
        log.info("Method " + jp.getSignature().getName() + " with args " + Arrays.toString(jp.getArgs())
                + " on the object " + jp.getTarget() + " was called successfully.");

    }

    @Pointcut("within(ru.clevertec.api..*) && !within(ru.clevertec.api.exc_handler.RestExceptionAdvice)")
    public void request() {
    }

    /**
     * логирование запросов к контроллерам. Поинткат определен в request()
     *
     * @param jp
     */
    @Before("request()")
    private void loggingRequest(JoinPoint jp) {
        log.info("request to " + jp.getTarget().getClass() +
                ", method " + jp.getSignature().getName() +
                ", with args " + Arrays.toString(jp.getArgs()));
    }

    @Pointcut("within(ru.clevertec.api..*) && !within(ru.clevertec.api.exc_handler..*)")
    public void response() {
    }

    /**
     * логирование ответов от контроллера. Поинткат определен в response()
     *
     * @param jp
     * @param returnValue
     */
    @AfterReturning(value = "response()", returning = "returnValue")
    private void loggingResponse(JoinPoint jp, Object returnValue) {
        log.info("response from " + jp.getTarget().getClass() +
                ", method " + jp.getSignature().getName() +
                ", with returned value " + returnValue);
    }

    @Pointcut("within(ru.clevertec.api.exc_handler.RestExceptionAdvice)")
    public void excAdvice() {
    }

    /**
     * логирование перехваченных исключений в RestExceptionAdvice. Поинткат определен в excAdvice()
     */
    @AfterReturning(value = "excAdvice()")
    private void loggingAdvice(JoinPoint jp) {
        Exception e = (Exception) jp.getArgs()[0];
        log.error(String.valueOf(e));
    }
}
