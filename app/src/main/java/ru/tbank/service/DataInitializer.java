package ru.tbank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.annotation.LogControllerExecution;
import ru.tbank.model.Category;
import ru.tbank.repository.CategoryRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.restTemplate = new RestTemplate();
    }


    @PostConstruct
    @LogControllerExecution
    public void init() {
        logger.info("Загрузка данных из API kudago начата.");
        try {
            String url = "https://kudago.com/public-api/v1.4/place-categories";
            Category[] categories = restTemplate.getForObject(url, Category[].class);
            if (categories != null) {
                Arrays.stream(categories).forEach(categoryRepository::save);
                logger.info("Данные загружены и сохранены успешно.");
            } else {
                logger.warn("Загруженные данные пусты.");
            }
        } catch (Exception e) {
            logger.error("Ошибка при загрузке данных: {}", e.getMessage());
        }
        logger.info("Загрузка данных завершена.");
    }
}