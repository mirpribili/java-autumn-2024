package ru.tbank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tbank.annotation.LogControllerExecution;
import ru.tbank.model.Category;
import ru.tbank.repository.CategoryRepository;
import ru.tbank.service.DataInitializer;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/places/categories")
@LogControllerExecution
public class CategoryController {
    private final CategoryRepository repository;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public CategoryController(CategoryRepository repository) {
        this.repository = repository;
    }
    @LogControllerExecution
    @GetMapping
    public Collection<Category> getAllCategories() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id) {
        logger.info("Получение категории с ID: {}", id);
        Category category = repository.findById(id);
        if (category == null) {
            logger.error("Ошибка при загрузке данных: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    //@LogExecutionTime
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = repository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody Category category) {
        Category updatedCategory = repository.update(id, category);
        if (updatedCategory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
