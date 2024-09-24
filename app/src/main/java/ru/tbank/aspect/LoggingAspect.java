package ru.tbank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.tbank.annotation.LogControllerExecution;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(logControllerExecution)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogControllerExecution logControllerExecution) throws Throwable {
        long start = System.currentTimeMillis();

        // Выполнение метода
        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        // Получение информации о методе и классе
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        logger.info("Метод {} класса {} выполнен за {} мс", methodName, className, executionTime);

        return proceed;
    }
}