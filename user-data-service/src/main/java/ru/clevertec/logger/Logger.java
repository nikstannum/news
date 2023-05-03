package ru.clevertec.logger;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
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

    @AfterThrowing(pointcut = "@annotation(LogInvocation)", throwing = "e")
    private void loggingError(Exception e) {
        log.error(String.valueOf(e));
    }
}
