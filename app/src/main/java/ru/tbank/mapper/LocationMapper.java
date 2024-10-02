package ru.tbank.mapper;

import ru.tbank.dto.LocationDTO;
import ru.tbank.model.Location;

public class LocationMapper {

    public static LocationDTO toDTO(Location location) {
        return new LocationDTO(location.getSlug(), location.getName());
    }

    public static Location toEntity(LocationDTO locationDTO) {
        return new Location(0, locationDTO.getSlug(), locationDTO.getName());
    }
}