package ru.tbank.workflow;

import ru.tbank.utils.CustomLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class LinkedListProcessingWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListProcessingWorkflow.class);
    private final CustomLinkedList<Integer> customLinkedList;

    public LinkedListProcessingWorkflow() {
        this.customLinkedList = new CustomLinkedList<>();
    }

    public void processLinkedList() {
        // Пример добавления элементов
        customLinkedList.add(1);
        customLinkedList.add(2);
        customLinkedList.add(3);

        // Логика работы с CustomLinkedList
        logger.info("Element at index 1: {}", customLinkedList.get(1));
        customLinkedList.remove(0);
        logger.info("List contains 2: {}", customLinkedList.contains(2));

        // Преобразование стрима в CustomLinkedList
        Stream<Integer> stream = Stream.of(4, 5, 6);
        stream.forEach(customLinkedList::add);

        // Использование reduce для суммирования элементов
        int sum = customLinkedList.stream().reduce(0, Integer::sum);
        logger.info("Sum of elements: {}", sum);
    }
}