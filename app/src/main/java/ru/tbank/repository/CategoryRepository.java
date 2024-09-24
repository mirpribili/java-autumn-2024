package ru.tbank.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tbank.model.Category;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import ru.tbank.service.DataInitializer;

@Repository
public class CategoryRepository {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final ConcurrentHashMap<Integer, Category> categories = new ConcurrentHashMap<>();
    private int currentId = 0;

    public Collection<Category> findAll() {
        return categories.values();
    }

    public Category findById(int id) {
        return categories.get(id);
    }

    public Category save(Category category) {
        category.setId(currentId++);
        logger.debug("Сохранена категория: {} с id {}", category, currentId);
        categories.put(category.getId(), category);
        return category;
    }

    public Category update(int id, Category category) {
        category.setId(id);
        return categories.replace(id, category);
    }

    public void delete(int id) {
        categories.remove(id);
    }
}
