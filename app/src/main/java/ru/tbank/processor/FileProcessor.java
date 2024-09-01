package ru.tbank.processor;

import ru.tbank.model.City;
import ru.tbank.parser.CityParserInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    private final CityParserInterface cityParser;

    public FileProcessor(CityParserInterface cityParser) {
        this.cityParser = cityParser;
    }

    public void processFile(File jsonFile) {
        String jsonFilePath = jsonFile.getAbsolutePath();
        String xmlFilePath = jsonFilePath.replace(".json", ".xml");

        try {
            City city = cityParser.parseCity(jsonFilePath);
            city.saveToXML(xmlFilePath);
            logger.info("Parsed city from file: {}", jsonFile.getName());
        } catch (RuntimeException e) {
            logger.error("Error parsing JSON file: {}", jsonFile.getName());
            logger.debug("Stacktrace:", e);
        }
    }
}