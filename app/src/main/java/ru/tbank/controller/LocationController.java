package ru.tbank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.annotation.LogControllerExecution;
import ru.tbank.dto.LocationDTO;
import ru.tbank.model.Location;
import ru.tbank.service.LocationService;
import ru.tbank.mapper.LocationMapper;
import ru.tbank.exception.LocationNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/locations")
@LogControllerExecution
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public Collection<LocationDTO> getAllLocations() {
        return locationService.getAllLocations().stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable int id) {
        try {
            Location location = locationService.getLocationById(id);
            return ResponseEntity.ok(LocationMapper.toDTO(location));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        Location createdLocation = locationService.createLocation(LocationMapper.toEntity(locationDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(LocationMapper.toDTO(createdLocation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable int id, @RequestBody LocationDTO locationDTO) {
        try {
            Location updatedLocation = locationService.updateLocation(id, LocationMapper.toEntity(locationDTO));
            return ResponseEntity.ok(LocationMapper.toDTO(updatedLocation));
        } catch (LocationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (LocationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}