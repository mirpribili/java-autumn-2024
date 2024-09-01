package ru.tbank.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.model.City;

import java.io.File;
import java.io.IOException;

public class CityParser implements CityParserInterface {
    private static final Logger logger = LoggerFactory.getLogger(CityParser.class);

    @Override
    public City parseCity(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            City city = mapper.readValue(new File(filePath), City.class);

            // Check if the city slug is missing or empty
            if (city.getSlug() == null || city.getSlug().isEmpty()) {
                logger.warn("City slug is missing or empty in file: {}", filePath);
            }

            // Check if coordinates are present
            City.Coordinates coords = city.getCoords();
            if (coords == null) {
                logger.warn("City coordinates are missing in file: {}", filePath);
            } else if (coords.getLatitude() < -90 || coords.getLatitude() > 90) {
                logger.warn("Invalid latitude value in file: {}", filePath);
            } else if (coords.getLongitude() < -180 || coords.getLongitude() > 180) {
                logger.warn("Invalid longitude value in file: {}", filePath);
            }

            return city;
        } catch (IOException e) {
            logger.error("Error parsing city JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}