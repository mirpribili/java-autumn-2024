package ru.tbank;

import ru.tbank.parser.CityParser;
import ru.tbank.parser.CityParserInterface;
import ru.tbank.processor.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.workflow.CityProcessingWorkflow;
import ru.tbank.workflow.LinkedListProcessingWorkflow;

import java.io.File;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // Обработка CustomLinkedList
        LinkedListProcessingWorkflow linkedListProcessingWorkflow = new LinkedListProcessingWorkflow();
        linkedListProcessingWorkflow.processLinkedList();

        // path to resources
        String resourcesPath = "app/src/main/resources/";
        // Обработка городов
        CityProcessingWorkflow cityProcessingWorkflow = new CityProcessingWorkflow(new CityParser());
        cityProcessingWorkflow.processCityFiles(resourcesPath);
    }
}