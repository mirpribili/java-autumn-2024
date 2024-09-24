package ru.tbank.workflow;

import ru.tbank.utils.CustomLinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

public class LinkedListProcessingWorkflow {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListProcessingWorkflow.class);
    private final CustomLinkedList<Integer> customLinkedList;
    private final CustomLinkedList<Integer> customLinkedList2;

    public LinkedListProcessingWorkflow() {
        this.customLinkedList = new CustomLinkedList<>();
        this.customLinkedList2 = new CustomLinkedList<>();
    }

    public void processLinkedList() {
        // Пример добавления элементов
        customLinkedList.add(1);
        customLinkedList.add(2);
        customLinkedList.add(3);
        customLinkedList.add(4);

        // Логика работы с CustomLinkedList
        logger.info("Element at index 1: {}", customLinkedList.get(1));
        customLinkedList.remove(0);
        logger.info("List contains 2: {}", customLinkedList.contains(2));

        // Преобразование стрима в CustomLinkedList с использованием reduce
        Stream<Integer> stream = Stream.of(1, 11, 10);

        // Используем reduce для создания нового CustomLinkedList
        CustomLinkedList<Integer> result = stream.reduce(
                new CustomLinkedList<Integer>(),
                (list, element) -> {
                    list.add(element);
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }
        );

        // Теперь result содержит элементы из стрима
        logger.info("Elements in the new CustomLinkedList: {}", result.stream().toArray());

        // Использование reduce для суммирования элементов
        int sum = result.stream().reduce(0, Integer::sum);
        logger.info("Sum of elements: {}", sum);

        // использование reduce для преобразования стрима в CustomLinkedList
        Stream<String> stringStream = Stream.of("apple", "banana", "cherry");
        CustomLinkedList<String> stringList = stringStream.reduce(
                new CustomLinkedList<>(),
                (list, item) -> {
                    list.add(item);
                    return list;
                },
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                }
        );
        for (String value : stringList) {
            logger.info("Elements in the CustomLinkedList: {}", value);
        }

        // произвольный стрим элементов преобразовать в CastomLinkedList
        Stream<Object> objectStream = Stream.of((Object) 1, (Object) 2, "banana", (Object) 4, (Object) 5);
        CustomLinkedList<Object> linkedList = CustomLinkedList.fromStream(objectStream);
        // Вывод элементов списка
        for (Object value : linkedList) {
            logger.info("value-> {}", value);
        }
    }
}