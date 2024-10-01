package ru.tbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.service.DataInitializer;

@SpringBootApplication
public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения...");
        SpringApplication.run(App.class, args);
        // http://localhost:8080/api/v1/places/categories
    }
}
