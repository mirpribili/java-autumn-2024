package ru.tbank.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class CategoryRepository {
    private final ConcurrentHashMap<Integer, Category> categories = new ConcurrentHashMap<>();
    private Integer currentId = 0;

    public Collection<Category> findAll() {
        return categories.values();
    }

    public Optional<Category> findById(int id) {
        return Optional.ofNullable(categories.get(id));
    }

    public Category save(Category category) {
        category.setId(currentId++);
        categories.put(category.getId(), category);
        return category;
    }

    public Category update(int id, Category category) {
        if (!categories.containsKey(id)) {
            log.warn("Попытка обновления несуществующей категории с ID: {}", id);
            throw new CategoryNotFoundException(id);
        }
        // Устанавливаем ID для обновленной категории
        category.setId(id);
        // Сохраняем категорию обратно в мапу
        categories.put(id, category); // put to replace
        return category; // Возвращаем обновленную категорию
    }

    public void delete(int id) {
        if (!categories.containsKey(id)) {
            log.warn("Попытка удаления несуществующей категории с ID: {}", id);
            throw new CategoryNotFoundException(id);
        }
        categories.remove(id);
    }

    public boolean existsById(int id) {
        return categories.containsKey(id);
    }
}