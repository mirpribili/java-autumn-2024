package ru.tbank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tbank.exception.LocationNotFoundException;
import ru.tbank.model.Location;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class LocationRepositoryTest {

    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        // Инициализация нового репозитория перед каждым тестом
        locationRepository = new LocationRepository();
    }

    @Test
    void testFindAll_ReturnsAllLocations() {
        // Arrange: Создание и сохранение локаций
        Location location1 = new Location(0, "location-1", "Город 1");
        Location location2 = new Location(0, "location-2", "Город 2");
        locationRepository.save(location1);
        locationRepository.save(location2);

        // Act: Получение всех локаций
        Collection<Location> locations = locationRepository.findAll();

        // Assert: Проверка количества сохраненных локаций
        assertEquals(2, locations.size());
    }

    @Test
    void testFindById_ReturnsLocation_WhenExists() {
        // Arrange: Создание и сохранение локации
        Location location = new Location(0, "location-1", "Город 1");
        locationRepository.save(location);

        // Act: Поиск локации по ID
        var foundLocation = locationRepository.findById(location.getId());

        // Assert: Проверка найденной локации
        assertTrue(foundLocation.isPresent());
        assertEquals(location.getName(), foundLocation.get().getName());
    }

    @Test
    void testFindById_ReturnsEmpty_WhenNotExists() {
        // Act: Поиск локации по несуществующему ID
        var foundLocation = locationRepository.findById(999);

        // Assert: Проверка отсутствия локации
        assertFalse(foundLocation.isPresent());
    }

    @Test
    void testSave_SavesLocationAndAssignsId() {
        // Arrange: Создание новой локации
        Location location = new Location(0, "location-1", "Город 1");

        // Act: Сохранение локации
        Location savedLocation = locationRepository.save(location);

        // Assert: Проверка назначения ID и сохранения данных
        assertNotNull(savedLocation.getId()); // ID должен быть назначен
        assertEquals(location.getName(), savedLocation.getName());
    }

    @Test
    void testUpdate_UpdatesExistingLocation() {
        // Arrange: Создание и сохранение локации
        Location location = new Location(0, "location-1", "Город 1");
        locationRepository.save(location);

        // Создание обновленной локации с тем же ID
        Location updatedLocation = new Location(0, "location-1-updated", "Обновленный Город");

        // Act: Обновление существующей локации
        Location result = locationRepository.update(location.getId(), updatedLocation);

        // Assert: Проверка обновленных данных
        assertEquals(updatedLocation.getName(), result.getName());
    }

    @Test
    void testUpdate_ThrowsException_WhenLocationNotFound() {
        // Arrange: Создание обновленной локации без существующего ID
        Location updatedLocation = new Location(0, "location-1-updated", "Обновленный Город");

        // Act & Assert: Проверка выброса исключения при обновлении несуществующей локации
        assertThrows(LocationNotFoundException.class, () ->
                locationRepository.update(999, updatedLocation) // ID не существует
        );
    }

    @Test
    void testDelete_RemovesExistingLocation() {
        // Arrange: Создание и сохранение локации
        Location location = new Location(0, "location-1", "Город 1");
        locationRepository.save(location);

        // Act: Удаление существующей локации
        locationRepository.delete(location.getId());

        // Assert: Проверка отсутствия удаленной локации
        assertFalse(locationRepository.existsById(location.getId())); // Локация должна быть удалена
    }

    @Test
    void testDelete_ThrowsException_WhenLocationNotFound() {
        // Act & Assert: Проверка выброса исключения при удалении несуществующей локации
        assertThrows(LocationNotFoundException.class, () ->
                locationRepository.delete(999) // ID не существует
        );
    }

    @Test
    void testExistsById_ReturnsTrue_WhenExists() {
        // Arrange: Создание и сохранение локации
        Location location = new Location(0, "location-1", "Город 1");
        locationRepository.save(location);

        // Act & Assert: Проверка существования локации по ID
        assertTrue(locationRepository.existsById(location.getId()));
    }

    @Test
    void testExistsById_ReturnsFalse_WhenNotExists() {
        // Act & Assert: Проверка отсутствия локации по несуществующему ID
        assertFalse(locationRepository.existsById(999)); // ID не существует
    }
}