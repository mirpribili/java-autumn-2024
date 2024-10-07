package ru.tbank.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.tbank.model.Category;
import ru.tbank.model.Location;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Testcontainers
@SpringBootTest
class DataInitializerIntegrationTest {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("place-categories", DataInitializerIntegrationTest.class, "/categories.json")
            .withMappingFromResource("locations", DataInitializerIntegrationTest.class, "/locations.json");

    @Autowired
    private DataInitializer dataInitializer;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("rest.kudago-service.host", wireMockContainer::getBaseUrl);
        registry.add("rest.kudago-service.methods.categories.type", () -> "GET");
        registry.add("rest.kudago-service.methods.categories.uri", () -> "/place-categories");
        registry.add("rest.kudago-service.methods.locations.type", () -> "GET");
        registry.add("rest.kudago-service.methods.locations.uri", () -> "/locations");

        System.out.println("WireMock URL: " + wireMockContainer.getBaseUrl());
    }

    @Test
    void init_shouldLoadCategoriesAndLocations() {
        categoryRepository.clear();
        locationRepository.clear();

        // Выполнение инициализации данных
        dataInitializer.init();

        Collection<Category> categoryCollection = categoryRepository.findAll();
        Collection<Location> locationCollection = locationRepository.findAll();


        // Ассерты для проверки количества загруженных категорий и городов
        Assertions.assertEquals(2, categoryCollection.size(), "Должно быть загружено 2 категории");
        Assertions.assertEquals(5, locationCollection.size(), "Должно быть загружено 5 городов");
    }
}