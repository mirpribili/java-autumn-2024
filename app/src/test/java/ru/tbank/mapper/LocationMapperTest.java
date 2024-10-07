package ru.tbank.mapper;

import org.junit.jupiter.api.Test;
import ru.tbank.dto.LocationDTO;
import ru.tbank.model.Location;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationMapperTest {
    @Test
    void testToDTO() {
        // Arrange
        Location location = new Location(1, "loc1", "Location 1");

        // Act
        LocationDTO locationDTO = LocationMapper.toDTO(location);

        // Assert
        assertEquals(location.getSlug(), locationDTO.getSlug());
        assertEquals(location.getName(), locationDTO.getName());
    }

    @Test
    void testToEntity() {
        // Arrange
        LocationDTO locationDTO = new LocationDTO("loc2", "Location 2");

        // Act
        Location location = LocationMapper.toEntity(locationDTO);

        // Assert
        assertEquals(0, location.getId());
        assertEquals(locationDTO.getSlug(), location.getSlug());
        assertEquals(locationDTO.getName(), location.getName());
    }

    @Test
    void testConstructor() {
        // Arrange & Act
        // Тестируем конструктор
        LocationMapper mapper = new LocationMapper();

        // Assert
        // Проверяем, что экземпляр создан
        assertEquals(mapper.getClass(), LocationMapper.class);
    }
}