package ru.tbank;

import ru.tbank.parser.CityParser;
import ru.tbank.parser.CityParserInterface;
import ru.tbank.processor.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        String resourcesPath = "src/main/resources/";
        File resourcesDir = new File(resourcesPath);
        File[] jsonFiles = resourcesDir.listFiles((dir, name) -> name.endsWith(".json"));

        CityParserInterface cityParser = new CityParser();
        FileProcessor fileProcessor = new FileProcessor(cityParser);

        if (jsonFiles != null) {
            logger.warn("Found {} JSON files in the resources directory.", jsonFiles.length);
            for (File jsonFile : jsonFiles) {
                logger.trace(" ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ > Processed: " + jsonFile.getName());
                fileProcessor.processFile(jsonFile);
            }
        } else {
            logger.warn("No JSON files found in the resources directory.");
        }
    }
}