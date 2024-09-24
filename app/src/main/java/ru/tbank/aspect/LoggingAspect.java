package ru.tbank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //@Around("(execution(* ru.tbank.controller..*(..)) && @target(ru.tbank.annotation.LogControllerExecution)) || @annotation(ru.tbank.annotation.LogMainExecution)")
    @Around("execution(* ru.tbank.controller..*(..)) && @target(ru.tbank.annotation.LogControllerExecution) || execution(* ru.tbank.service..*(..)) && @annotation(ru.tbank.annotation.LogMainExecution)")
    //@Around("(@target(ru.tbank.annotation.LogControllerExecution) || @annotation(ru.tbank.annotation.LogMainExecution))")
    public Object combinedAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        logger.info("Запуск метода: {}", joinPoint.getSignature().getName());

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("Метод {} выполнен за {} мс", joinPoint.getSignature().getName(), executionTime);

        return proceed;
    }
}