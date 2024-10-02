package ru.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tbank.exception.LocationNotFoundException;
import ru.tbank.model.Location;
import ru.tbank.repository.LocationRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLocations() {
        // Arrange
        Location location1 = new Location(1, "loc1", "Location 1");
        Location location2 = new Location(2, "loc2", "Location 2");
        when(locationRepository.findAll()).thenReturn(Arrays.asList(location1, location2));

        // Act
        Collection<Location> locations = locationService.getAllLocations();

        // Assert
        assertEquals(2, locations.size());
        verify(locationRepository, times(1)).findAll();
    }

    @Test
    void getLocationById() {
        // Arrange
        Location location = new Location(1, "loc1", "Location 1");
        when(locationRepository.findById(anyInt())).thenReturn(Optional.of(location));

        // Act
        Location foundLocation = locationService.getLocationById(1);

        // Assert
        assertEquals(location, foundLocation);
        verify(locationRepository, times(1)).findById(1);
    }

    @Test
    void getLocationById_NotFound() {
        // Arrange
        when(locationRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () -> locationService.getLocationById(999));
    }

    @Test
    void createLocation() {
        // Arrange
        Location newLocation = new Location(0, "loc3", "Location 3");
        when(locationRepository.save(any(Location.class))).thenReturn(newLocation);

        // Act
        Location createdLocation = locationService.createLocation(newLocation);

        // Assert
        assertEquals(newLocation, createdLocation);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void updateLocation() {
        // Arrange
        Location updatedLocation = new Location(1, "loc1-updated", "Updated Location");
        when(locationRepository.existsById(anyInt())).thenReturn(true);
        when(locationRepository.update(anyInt(), any(Location.class))).thenReturn(updatedLocation);

        // Act
        Location result = locationService.updateLocation(1, updatedLocation);

        // Assert
        assertEquals(updatedLocation, result);
        verify(locationRepository, times(1)).existsById(1);
        verify(locationRepository, times(1)).update(eq(1), any(Location.class));
    }

    @Test
    void updateLocation_NotFound() {
        // Arrange
        when(locationRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () -> locationService.updateLocation(999, new Location()));
    }

    @Test
    void deleteLocation() {
        // Arrange
        when(locationRepository.existsById(anyInt())).thenReturn(true);

        // Act
        locationService.deleteLocation(1);

        // Assert
        verify(locationRepository, times(1)).delete(1);
    }

    @Test
    void deleteLocation_NotFound() {
        // Arrange
        when(locationRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(LocationNotFoundException.class, () -> locationService.deleteLocation(999));
    }
}