package ru.tbank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import ru.tbank.annotation.Logging;

@Aspect
@Component
public class LoggingAspect {
    @Logging
    private Logger logger;

    @Around("execution(* ru.tbank.controller..*(..)) && @target(ru.tbank.annotation.LogControllerExecution) || execution(* ru.tbank.service..*(..)) && @annotation(ru.tbank.annotation.LogMainExecution)")
    public Object combinedAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        logger.info("Запуск метода: {}", joinPoint.getSignature().getName());

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("Метод {} выполнен за {} мс", joinPoint.getSignature().getName(), executionTime);

        return proceed;
    }
}