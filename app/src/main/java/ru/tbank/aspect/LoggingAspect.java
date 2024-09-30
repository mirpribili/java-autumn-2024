package ru.tbank.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Slf4j
@Aspect
@Component
public class LoggingAspect {


    @Around("execution(* ru.tbank.controller..*(..)) && @target(ru.tbank.annotation.LogControllerExecution) || execution(* ru.tbank.service..*(..)) && @annotation(ru.tbank.annotation.LogMainExecution)")
    public Object combinedAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("Запуск метода: {}", joinPoint.getSignature().getName());

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        log.info("Метод {} выполнен за {} мс", joinPoint.getSignature().getName(), executionTime);

        return proceed;
    }
}