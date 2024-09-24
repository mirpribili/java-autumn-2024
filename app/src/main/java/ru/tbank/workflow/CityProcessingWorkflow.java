package ru.tbank.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.parser.CityParserInterface;
import ru.tbank.processor.FileProcessor;

import java.io.File;

public class CityProcessingWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(CityProcessingWorkflow.class);
    private final CityParserInterface cityParser;
    private final FileProcessor fileProcessor;

    public CityProcessingWorkflow(CityParserInterface cityParser) {
        this.cityParser = cityParser;
        this.fileProcessor = new FileProcessor(cityParser);
    }

    public File[] getJsonFiles(String resourcesPath) {
        File resourcesDir = new File(resourcesPath);
        logger.info("Проверяем директорию: {}", resourcesDir.getAbsolutePath());

        if (resourcesDir.exists()) {
            logger.info("Директория существует.");
            if (resourcesDir.isDirectory()) {
                logger.info("Это директория.");

                File[] allFiles = resourcesDir.listFiles();
                if (allFiles != null) {
                    logger.info("Всего файлов в директории: {}", allFiles.length);
                    for (File file : allFiles) {
                        logger.info("Найден файл: {}", file.getName());
                    }
                } else {
                    logger.warn("Не удалось получить список файлов в директории.");
                }

                // Получаем только JSON файлы
                File[] jsonFiles = resourcesDir.listFiles((dir, name) -> {
                    boolean isJson = name.endsWith(".json");
                    logger.info("Проверка файла: {} - JSON: {}", name, isJson);
                    return isJson;
                });

                logger.info("Найдено JSON файлов: {}", jsonFiles != null ? jsonFiles.length : 0);
                return jsonFiles;
            } else {
                logger.warn("Это не директория!");
            }
        } else {
            logger.warn("Директория не существует!");
        }
        return null;
    }

    public void processCityFiles(String directoryPath) {
        // Получаем текущий рабочий каталог
        String currentWorkingDir = System.getProperty("user.dir");

        // Логируем текущий рабочий каталог
        logger.info("Текущий рабочий каталог: {}", currentWorkingDir);
        logger.info("Ищем JSON файлы в директории: {}", directoryPath);

        // get files
        File[] jsonFiles = getJsonFiles(directoryPath);

        if (jsonFiles != null) {
            logger.warn("Найдено {} JSON файлов в директории.", jsonFiles.length);
            for (File jsonFile : jsonFiles) {
                logger.trace(" ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ ╰(▔∀▔)╯ > Обрабатываем: " + jsonFile.getName());
                fileProcessor.processFile(jsonFile);
            }
        } else {
            logger.warn("JSON файлы не найдены в директории.");
        }
    }
}