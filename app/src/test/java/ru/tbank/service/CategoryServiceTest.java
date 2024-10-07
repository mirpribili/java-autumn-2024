package ru.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;
import ru.tbank.repository.CategoryRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories() {
        // Arrange
        Category category1 = new Category(1, "cat1", "Category 1");
        Category category2 = new Category(2, "cat2", "Category 2");
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // Act
        Collection<Category> categories = categoryService.getAllCategories();

        // Assert
        assertEquals(2, categories.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById() {
        // Arrange
        Category category = new Category(1, "cat1", "Category 1");
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.of(category));

        // Act
        Category foundCategory = categoryService.getCategoryById(1);

        // Assert
        assertEquals(category, foundCategory);
        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    void getCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(999));
    }

    @Test
    void createCategory() {
        // Arrange
        Category newCategory = new Category(0, "cat3", "Category 3");
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category createdCategory = categoryService.createCategory(newCategory);

        // Assert
        assertEquals(newCategory, createdCategory);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory() {
        // Arrange
        Category updatedCategory = new Category(1, "cat1-updated", "Updated Category");

        when(categoryRepository.existsById(anyInt())).thenReturn(true);
        when(categoryRepository.update(anyInt(), any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(1, updatedCategory);

        // Assert
        assertEquals(updatedCategory, result);
        verify(categoryRepository, times(1)).existsById(1);
        verify(categoryRepository, times(1)).update(eq(1), any(Category.class));
    }

    @Test
    void updateCategory_NotFound() {
        // Arrange
        when(categoryRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(999, new Category()));
    }

    @Test
    void deleteCategory() {
        // Arrange
        when(categoryRepository.existsById(anyInt())).thenReturn(true);

        // Act
        categoryService.deleteCategory(1);

        // Assert
        verify(categoryRepository, times(1)).delete(1);
    }

    @Test
    void deleteCategory_NotFound() {
        // Arrange
        when(categoryRepository.existsById(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(999));
    }
}