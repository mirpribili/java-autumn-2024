package ru.tbank.service;

import org.springframework.stereotype.Service;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;
import ru.tbank.repository.CategoryRepository;

import java.util.Collection;
import java.util.Optional;

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
        return Optional.ofNullable(repository.findById(id))
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category createCategory(Category category) {
        return repository.save(category);
    }

    public Category updateCategory(int id, Category category) {
        return Optional.ofNullable(repository.update(id, category))
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public void deleteCategory(int id) {
        repository.delete(id);
    }
}