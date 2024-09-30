package ru.tbank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.annotation.LogControllerExecution;
import ru.tbank.annotation.LogMainExecution;
import ru.tbank.annotation.Logging;
import ru.tbank.model.Category;
import ru.tbank.model.Location; // Импортируем Location
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository; // Импортируем репозиторий городов

import java.util.Arrays;

@Service
public class DataInitializer implements ApplicationRunner {
    @Logging
    private Logger logger;

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository; // Добавляем репозиторий городов
    private final RestTemplate restTemplate;

    public DataInitializer(CategoryRepository categoryRepository, LocationRepository locationRepository) {
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository; // Инициализируем репозиторий городов
        this.restTemplate = new RestTemplate();
    }

    public void init() {
        logger.info("Загрузка данных из API kudago начата.");

        try {
            // Загрузка категорий
            String categoriesUrl = "https://kudago.com/public-api/v1.4/place-categories";
            try {
                Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
                if (categories != null) {
                    Arrays.stream(categories).forEach(categoryRepository::save);
                    logger.info("Категории загружены и сохранены успешно.");
                } else {
                    logger.warn("Загруженные категории пусты.");
                }
            } catch (RestClientException e) {
                logger.error("Ошибка при загрузке категорий: {}", e.getMessage());
            }

            // Загрузка городов
            String locationsUrl = "https://kudago.com/public-api/v1.4/locations";
            try {
                Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
                if (locations != null) {
                    Arrays.stream(locations).forEach(locationRepository::save);
                    logger.info("Города загружены и сохранены успешно.");
                } else {
                    logger.warn("Загруженные города пусты.");
                }
            } catch (RestClientException e) {
                logger.error("Ошибка при загрузке городов: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Ошибка при загрузке данных: {}", e.getMessage());
        }

        logger.info("Загрузка данных завершена.");
    }

    @LogMainExecution
    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }
}