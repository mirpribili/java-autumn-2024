package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.annotation.LogMainExecution;
import ru.tbank.model.Category;
import ru.tbank.model.Location;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository;

import java.util.Arrays;
@Slf4j
@Service
public class DataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository; // Добавляем репозиторий городов
    private final RestTemplate restTemplate;

    @Value("${rest.kudago-service.host}")
    private String baseUrl;

    public DataInitializer(CategoryRepository categoryRepository, LocationRepository locationRepository, RestTemplate restTemplate) {
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository; // Инициализируем репозиторий городов
        this.restTemplate = restTemplate;
    }

    public void init() {
        log.info("Загрузка данных из API kudago начата.");

        try {
            // Загрузка категорий
            String categoriesUrl = baseUrl + "/place-categories";
            try {
                Category[] categories = restTemplate.getForObject(categoriesUrl, Category[].class);
                if (categories != null) {
                    Arrays.stream(categories).forEach(categoryRepository::save);
                    log.info("Категории загружены и сохранены успешно.");
                } else {
                    log.warn("Загруженные категории пусты.");
                }
            } catch (RestClientException e) {
                log.error("Ошибка при загрузке категорий: {}", e.getMessage());
            }

            // Загрузка городов
            String locationsUrl = baseUrl + "/locations";
            try {
                Location[] locations = restTemplate.getForObject(locationsUrl, Location[].class);
                if (locations != null) {
                    Arrays.stream(locations).forEach(locationRepository::save);
                    log.info("Города загружены и сохранены успешно.");
                } else {
                    log.warn("Загруженные города пусты.");
                }
            } catch (RestClientException e) {
                log.error("Ошибка при загрузке городов: {}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Ошибка при загрузке данных: {}", e.getMessage());
        }

        log.info("Загрузка данных завершена.");
    }

    @LogMainExecution
    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
    }
}