package ru.tbank.parser;

import ru.tbank.model.City;

public interface CityParserInterface {
    City parseCity(String filePath);
}