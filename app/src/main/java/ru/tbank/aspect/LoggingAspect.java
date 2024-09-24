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
    public Object logControllerExecution(ProceedingJoinPoint joinPoint, LogControllerExecution logControllerExecution) throws Throwable {
        logger.info("Вызов метода: {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}