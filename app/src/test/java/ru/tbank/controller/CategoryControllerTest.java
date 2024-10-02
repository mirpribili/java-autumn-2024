package ru.tbank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.tbank.model.Category;
import ru.tbank.service.CategoryService;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    @InjectMocks
    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories() {
        // Arrange
        Category category1 = new Category(1, "cat1", "Category 1");
        Category category2 = new Category(2, "cat2", "Category 2");
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category1, category2));

        // Act
        Collection<Category> categories = categoryController.getAllCategories();

        // Assert
        assertEquals(2, categories.size());
        verify(categoryService, times(1)).getAllCategories(); // Убедитесь, что метод сервиса был вызван один раз
    }

    @Test
    void getCategoryById() {
        // Arrange
        Category category = new Category(1, "cat1", "Category 1");
        when(categoryService.getCategoryById(anyInt())).thenReturn(category);

        // Act
        ResponseEntity<Category> response = categoryController.getCategoryById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoryService, times(1)).getCategoryById(1); // Убедитесь, что метод сервиса был вызван один раз
    }

    @Test
    void createCategory() {
        // Arrange
        Category newCategory = new Category(0, "cat3", "Category 3");
        Category createdCategory = new Category(3, "cat3", "Category 3");
        when(categoryService.createCategory(any(Category.class))).thenReturn(createdCategory);

        // Act
        ResponseEntity<Category> response = categoryController.createCategory(newCategory);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdCategory, response.getBody());
        verify(categoryService, times(1)).createCategory(newCategory); // Убедитесь, что метод сервиса был вызван один раз
    }

    @Test
    void updateCategory() {
        // Arrange
        Category updatedCategory = new Category(1, "cat1-updated", "Updated Category");
        when(categoryService.updateCategory(anyInt(), any(Category.class))).thenReturn(updatedCategory);

        // Act
        ResponseEntity<Category> response = categoryController.updateCategory(1, updatedCategory);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
        verify(categoryService, times(1)).updateCategory(1, updatedCategory); // Убедитесь, что метод сервиса был вызван один раз
    }

    @Test
    void deleteCategory() {
        // Act
        ResponseEntity<Void> response = categoryController.deleteCategory(1);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(1); // Убедитесь, что метод сервиса был вызван один раз
    }
}