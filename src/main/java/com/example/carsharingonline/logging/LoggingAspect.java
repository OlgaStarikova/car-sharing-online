package com.example.carsharingonline.logging;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Указываем, где применять аспект:
     * все публичные методы внутри пакета service и его подпакетов.
     */
    @Pointcut("execution(public * com.example.carsharingonline.service..*(..))")
    public void serviceMethods() {

    }

    /**
     * Логируем вход в метод
     */
    @Before("serviceMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());
        log.debug("➡️ Entering method: {} with arguments: {}", methodName, args);
        log.info("➡️ Entering method: {} with arguments: {}", methodName, args);
    }

    /**
     * Логируем успешный выход из метода
     */
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().toShortString();
        log.debug("✅ Exiting method: {} with result: {}", methodName, result);
    }

    /**
     * Логируем исключения, если они произошли
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        log.error("💥 Exception in method: {} -> {}", methodName, ex.getMessage());
    }
}
