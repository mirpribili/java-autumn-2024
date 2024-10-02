package ru.tbank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        log.info("Запуск приложения...");
        SpringApplication.run(App.class, args);
        // http://localhost:8080/api/v1/places/categories
    }
}
