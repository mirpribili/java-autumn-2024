package ru.tbank.repository;

import org.springframework.stereotype.Repository;
import ru.tbank.model.Location;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LocationRepository {

    private final ConcurrentHashMap<Integer, Location> locations = new ConcurrentHashMap<>();
    private Integer currentId = 0;

    public Collection<Location> findAll() {
        return locations.values();
    }

    public Optional<Location> findById(int id) {
        return Optional.ofNullable(locations.get(id));
    }

    public Location save(Location location) {
        location.setId(currentId++);
        locations.put(location.getId(), location);
        return location;
    }

    public Location update(int id, Location location) {
        location.setId(id);
        return locations.replace(id, location);
    }

    public void delete(int id) {
        locations.remove(id);
    }

    public boolean existsById(int id) {
        return locations.containsKey(id);
    }
}