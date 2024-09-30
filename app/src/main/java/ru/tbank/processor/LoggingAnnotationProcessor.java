package ru.tbank.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.tbank.annotation.Logging;

import java.lang.reflect.Field;

@Component
public class LoggingAnnotationProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Logging.class)) {
                if (field.getType().equals(Logger.class)) {
                    field.setAccessible(true);
                    try {
                        Logger logger = LoggerFactory.getLogger(bean.getClass());
                        field.set(bean, logger);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bean;
    }
}