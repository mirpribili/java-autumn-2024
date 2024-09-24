package ru.tbank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.annotation.LogControllerExecution;
import ru.tbank.model.Location;
import ru.tbank.repository.LocationRepository;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/locations")
@LogControllerExecution
public class LocationController {
    private final LocationRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    public LocationController(LocationRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Collection<Location> getAllLocations() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable int id) {
        logger.info("Получение города с ID: {}", id);
        Location location = repository.findById(id);
        if (location == null) {
            logger.error("Город с ID {} не найден", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(location);
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        Location createdLocation = repository.save(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable int id, @RequestBody Location location) {
        Location updatedLocation = repository.update(id, location);
        if (updatedLocation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedLocation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable int id) {
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
}