package ru.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.model.Category;
import ru.tbank.model.Location;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DataInitializer dataInitializer;

    // Установка значений свойств перед каждым тестом
    private String baseUrl = "https://kudago.com/public-api/v1.4";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Установка базового URL в DataInitializer
        dataInitializer.setBaseUrl(baseUrl);
    }

    @Test
    void testInit_SuccessfulCategoryAndLocationLoad() {
        Category[] categories = {
                new Category(1, "category-1", "Категория 1"),
                new Category(2, "category-2", "Категория 2")
        };
        Location[] locations = {
                new Location(1, "location-1", "Город 1"),
                new Location(2, "location-2", "Город 2")
        };

        when(restTemplate.getForObject(baseUrl + "/place-categories", Category[].class)).thenReturn(categories);
        when(restTemplate.getForObject(baseUrl + "/locations", Location[].class)).thenReturn(locations);

        dataInitializer.init();

        verify(categoryRepository, times(2)).save(any(Category.class));
        verify(locationRepository, times(2)).save(any(Location.class));
    }

    @Test
    void testInit_EmptyCategoriesResponse() {
        when(restTemplate.getForObject(baseUrl + "/place-categories", Category[].class)).thenReturn(new Category[0]);

        Location[] locations = {
                new Location(1, "location-1", "Город 1"),
                new Location(2, "location-2", "Город 2")
        };
        when(restTemplate.getForObject(baseUrl + "/locations", Location[].class)).thenReturn(locations);

        dataInitializer.init();

        verify(categoryRepository, never()).save(any(Category.class)); // Не должно быть сохранений категорий
        verify(locationRepository, times(2)).save(any(Location.class)); // Должны сохраниться города
    }

    @Test
    void testInit_EmptyLocationsResponse() {
        Category[] categories = {
                new Category(1, "category-1", "Категория 1"),
                new Category(2, "category-2", "Категория 2")
        };
        when(restTemplate.getForObject(baseUrl + "/place-categories", Category[].class)).thenReturn(categories);

        when(restTemplate.getForObject(baseUrl + "/locations", Location[].class)).thenReturn(new Location[0]);

        dataInitializer.init();

        verify(categoryRepository, times(2)).save(any(Category.class)); // Должны сохраниться категории
        verify(locationRepository, never()).save(any(Location.class)); // Не должно быть сохранений городов
    }

    @Test
    void testInit_ExceptionDuringCategoriesLoad() {
        when(restTemplate.getForObject(baseUrl + "/place-categories", Category[].class))
                .thenThrow(new RestClientException("Ошибка при загрузке категорий"));

        Location[] locations = {
                new Location(1, "location-1", "Город 1"),
                new Location(2, "location-2", "Город 2")
        };
        when(restTemplate.getForObject(baseUrl + "/locations", Location[].class)).thenReturn(locations);

        dataInitializer.init();

        verify(categoryRepository, never()).save(any(Category.class)); // Категории не должны быть сохранены

        verify(locationRepository, times(2)).save(any(Location.class)); // Города должны сохраниться несмотря на исключение с категориями
    }

    @Test
    void testInit_ExceptionDuringLocationsLoad() {
        Category[] categories = {
                new Category(1, "category-1", "Категория 1"),
                new Category(2, "category-2", "Категория 2")
        };

        when(restTemplate.getForObject(baseUrl + "/place-categories", Category[].class)).thenReturn(categories);

        when(restTemplate.getForObject(baseUrl + "/locations", Location[].class))
                .thenThrow(new RestClientException("Ошибка при загрузке городов"));

        dataInitializer.init();

        verify(locationRepository, never()).save(any(Location.class)); // Города не должны быть сохранены

        verify(categoryRepository, times(2)).save(any(Category.class)); // Категории должны сохраниться несмотря на исключение с городами
    }
}