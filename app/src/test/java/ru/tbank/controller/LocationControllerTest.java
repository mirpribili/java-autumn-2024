package ru.tbank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tbank.dto.LocationDTO;
import ru.tbank.exception.LocationNotFoundException;
import ru.tbank.mapper.LocationMapper;
import ru.tbank.model.Location;
import ru.tbank.service.LocationService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationControllerTest {

    @InjectMocks
    private LocationController locationController;

    @Mock
    private LocationService locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLocations() {
        // Arrange
        Location location1 = new Location(1, "loc1", "Location 1");
        Location location2 = new Location(2, "loc2", "Location 2");
        when(locationService.getAllLocations()).thenReturn(Arrays.asList(location1, location2));

        // Act
        Collection<LocationDTO> locations = locationController.getAllLocations();

        // Assert
        assertEquals(2, locations.size());
        verify(locationService, times(1)).getAllLocations();
    }

    @Test
    void getLocationById() {
        // Arrange
        Location location = new Location(1, "loc1", "Location 1");
        when(locationService.getLocationById(anyInt())).thenReturn(location);

        // Act
        ResponseEntity<LocationDTO> response = locationController.getLocationById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LocationMapper.toDTO(location), response.getBody());
        verify(locationService, times(1)).getLocationById(1);
    }

    @Test
    void getLocationById_NotFound() {
        // Arrange
        when(locationService.getLocationById(anyInt())).thenThrow(new LocationNotFoundException(999));

        // Act
        ResponseEntity<LocationDTO> response = locationController.getLocationById(999);

        // Assert that the response status is NOT_FOUND (404)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createLocation() {
        // Arrange
        LocationDTO newLocationDTO = new LocationDTO("loc3", "Location 3");
        Location createdLocation = new Location(3, "loc3", "Location 3");
        when(locationService.createLocation(any(Location.class))).thenReturn(createdLocation);

        // Act
        ResponseEntity<LocationDTO> response = locationController.createLocation(newLocationDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(LocationMapper.toDTO(createdLocation), response.getBody());
        verify(locationService, times(1)).createLocation(any(Location.class));
    }

    @Test
    void updateLocation() {
        // Arrange
        Location updatedLocation = new Location(1, "loc1-updated", "Updated Location");
        when(locationService.updateLocation(anyInt(), any(Location.class))).thenReturn(updatedLocation);

        // Act
        ResponseEntity<LocationDTO> response = locationController.updateLocation(1, new LocationDTO("loc1-updated", "Updated Location"));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LocationMapper.toDTO(updatedLocation), response.getBody());
        verify(locationService, times(1)).updateLocation(eq(1), any(Location.class));
    }

    @Test
    void updateLocation_NotFound() {
        // Arrange
        when(locationService.updateLocation(anyInt(), any(Location.class))).thenThrow(new LocationNotFoundException(999));

        // Act
        ResponseEntity<LocationDTO> response = locationController.updateLocation(999, new LocationDTO("loc1-updated", "Updated Location"));

        // Assert that the response status is NOT_FOUND (404)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteLocation() {
        // Act
        ResponseEntity<Void> response = locationController.deleteLocation(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(locationService, times(1)).deleteLocation(1);
    }

    @Test
    void deleteLocation_NotFound() {
        // Arrange
        doThrow(new LocationNotFoundException(999)).when(locationService).deleteLocation(anyInt());

        // Act
        ResponseEntity<Void> response = locationController.deleteLocation(999);

        // Assert that the response status is NOT_FOUND (404)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}