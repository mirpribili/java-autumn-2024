package ru.tbank.service;

import org.springframework.stereotype.Service;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;
import ru.tbank.repository.CategoryRepository;

import java.util.Collection;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public Collection<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategoryById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category createCategory(Category category) {
        return repository.save(category);
    }

    public Category updateCategory(int id, Category category) {
        if (!repository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        return repository.update(id, category);
    }

    public void deleteCategory(int id) {
        if (!repository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        repository.delete(id);
    }
}