package ru.tbank.repository;

import lombok.extern.slf4j.Slf4j;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
@Slf4j
@Repository
public class CategoryRepository {
    private final ConcurrentHashMap<Integer, Category> categories = new ConcurrentHashMap<>();
    private Integer currentId = 0;

    public Collection<Category> findAll() {
        return categories.values();
    }

    public Category findById(int id) {
        return categories.get(id);
    }

    public Category save(Category category) {
        category.setId(currentId++);
        categories.put(category.getId(), category);
        return category;
    }

    public Category update(int id, Category category) {
        if (!categories.containsKey(id)) {
            log.warn("Попытка обновления несуществующей категории с ID: {}", id);
            throw new CategoryNotFoundException(id); // Выбрасываем исключение
        }
        category.setId(id);
        return categories.replace(id, category);
    }

    public void delete(int id) {
        if (!categories.containsKey(id)) {
            log.warn("Попытка удаления несуществующей категории с ID: {}", id);
            throw new CategoryNotFoundException(id); // Выбрасываем исключение
        }
        categories.remove(id);
    }

    public boolean existsById(int id) {
        return categories.containsKey(id);
    }
}