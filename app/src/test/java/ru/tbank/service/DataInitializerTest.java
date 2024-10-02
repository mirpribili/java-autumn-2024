package ru.tbank.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.tbank.model.Category;
import ru.tbank.model.Location;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.repository.LocationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DataInitializer dataInitializer;

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

        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/place-categories", Category[].class)).thenReturn(categories);
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/locations", Location[].class)).thenReturn(locations);

        dataInitializer.init();

        verify(categoryRepository, times(2)).save(any(Category.class));
        verify(locationRepository, times(2)).save(any(Location.class));
    }

    @Test
    void testInit_EmptyCategoriesResponse() {
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/place-categories", Category[].class)).thenReturn(new Category[0]);

        Location[] locations = {
                new Location(1, "location-1", "Город 1"),
                new Location(2, "location-2", "Город 2")
        };
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/locations", Location[].class)).thenReturn(locations);

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
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/place-categories", Category[].class)).thenReturn(categories);

        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/locations", Location[].class)).thenReturn(new Location[0]);

        dataInitializer.init();

        verify(categoryRepository, times(2)).save(any(Category.class));
        // Должны сохраниться категории
        verify(locationRepository, never()).save(any(Location.class));
        // Не должно быть сохранений городов
    }

    @Test
    void testInit_ExceptionDuringCategoriesLoad() {
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/place-categories", Category[].class))
                .thenThrow(new RestClientException("Ошибка при загрузке категорий"));

        Location[] locations = {
                new Location(1, "location-1", "Город 1"),
                new Location(2, "location-2", "Город 2")
        };
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/locations", Location[].class)).thenReturn(locations);

        dataInitializer.init();

        // Проверка взаимодействия с репозиториями (категории не должны быть сохранены)
        verify(categoryRepository, never()).save(any(Category.class));

        // Города должны сохраниться несмотря на исключение с категориями
        verify(locationRepository, times(2)).save(any(Location.class));
    }

    @Test
    void testInit_ExceptionDuringLocationsLoad() {
        Category[] categories = {
                new Category(1, "category-1", "Категория 1"),
                new Category(2, "category-2", "Категория 2")
        };
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/place-categories", Category[].class)).thenReturn(categories);

        // Настройка исключения при загрузке городов от API
        when(restTemplate.getForObject("https://kudago.com/public-api/v1.4/locations", Location[].class))
                .thenThrow(new RestClientException("Ошибка при загрузке городов"));

        dataInitializer.init();

        // Города не должны быть сохранены
        verify(locationRepository, never()).save(any(Location.class));

        // Категории должны сохраниться несмотря на исключение с городами
        verify(categoryRepository, times(2)).save(any(Category.class));
    }
}