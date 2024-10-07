package ru.tbank.mapper;

import lombok.NoArgsConstructor;
import ru.tbank.dto.LocationDTO;
import ru.tbank.model.Location;

@NoArgsConstructor
public class LocationMapper {

    public static LocationDTO toDTO(Location location) {
        return new LocationDTO(location.getSlug(), location.getName());
    }

    public static Location toEntity(LocationDTO locationDTO) {
        return new Location(0, locationDTO.getSlug(), locationDTO.getName());
    }
}