package ru.tbank.service;

import org.springframework.stereotype.Service;
import ru.tbank.exception.LocationNotFoundException;
import ru.tbank.model.Location;
import ru.tbank.repository.LocationRepository;

import java.util.Collection;

@Service
public class LocationService {
    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public Collection<Location> getAllLocations() {
        return repository.findAll();
    }

    public Location getLocationById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));
    }

    public Location createLocation(Location location) {
        return repository.save(location);
    }

    public Location updateLocation(int id, Location location) {
        if (!repository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }
        return repository.update(id, location);
    }

    public void deleteLocation(int id) {
        if (!repository.existsById(id)) {
            throw new LocationNotFoundException(id);
        }
        repository.delete(id);
    }
}