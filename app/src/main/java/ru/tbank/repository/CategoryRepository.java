package ru.tbank.repository;

import ru.tbank.model.Category;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepository {
    private final ConcurrentHashMap<Integer, Category> categories = new ConcurrentHashMap<>();
    private int currentId = 1;

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
        category.setId(id);
        return categories.replace(id, category);
    }

    public void delete(int id) {
        categories.remove(id);
    }
}
