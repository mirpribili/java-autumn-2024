package ru.tbank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tbank.exception.CategoryNotFoundException;
import ru.tbank.model.Category;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {

    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // Инициализация нового репозитория перед каждым тестом
        categoryRepository = new CategoryRepository();
    }

    @Test
    void testFindAll_ReturnsAllCategories() {
        // Arrange: Создание и сохранение категорий
        Category category1 = new Category(0, "category-1", "Категория 1");
        Category category2 = new Category(0, "category-2", "Категория 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // Act: Получение всех категорий
        Collection<Category> categories = categoryRepository.findAll();

        // Assert: Проверка количества сохраненных категорий
        assertEquals(2, categories.size());
    }

    @Test
    void testFindById_ReturnsCategory_WhenExists() {
        // Arrange: Создание и сохранение категории
        Category category = new Category(0, "category-1", "Категория 1");
        categoryRepository.save(category);

        // Act: Поиск категории по ID
        var foundCategory = categoryRepository.findById(category.getId());

        // Assert: Проверка найденной категории
        assertTrue(foundCategory.isPresent());
        assertEquals(category.getName(), foundCategory.get().getName());
    }

    @Test
    void testFindById_ReturnsEmpty_WhenNotExists() {
        // Act: Поиск категории по несуществующему ID
        var foundCategory = categoryRepository.findById(999);

        // Assert: Проверка отсутствия категории
        assertFalse(foundCategory.isPresent());
    }

    @Test
    void testSave_SavesCategoryAndAssignsId() {
        // Arrange: Создание новой категории
        Category category = new Category(0, "category-1", "Категория 1");

        // Act: Сохранение категории
        Category savedCategory = categoryRepository.save(category);

        // Assert: Проверка назначения ID и сохранения данных
        assertNotNull(savedCategory.getId()); // ID должен быть назначен
        assertEquals(category.getName(), savedCategory.getName());
    }

    @Test
    void testUpdate_UpdatesExistingCategory() {
        // Arrange: Создание и сохранение категории
        Category category = new Category(0, "category-1", "Категория 1");
        categoryRepository.save(category);

        // Создание обновленной категории с тем же ID
        Category updatedCategory = new Category(0, "category-1-updated", "Обновленная Категория");

        // Act: Обновление существующей категории
        Category result = categoryRepository.update(category.getId(), updatedCategory);

        // Assert: Проверка обновленных данных
        assertEquals(updatedCategory.getName(), result.getName());
    }

    @Test
    void testUpdate_ThrowsException_WhenCategoryNotFound() {
        // Arrange: Создание обновленной категории без существующего ID
        Category updatedCategory = new Category(0, "category-1-updated", "Обновленная Категория");

        // Act & Assert: Проверка выброса исключения при обновлении несуществующей категории
        assertThrows(CategoryNotFoundException.class, () ->
                categoryRepository.update(999, updatedCategory) // ID не существует
        );
    }

    @Test
    void testDelete_RemovesExistingCategory() {
        // Arrange: Создание и сохранение категории
        Category category = new Category(0, "category-1", "Категория 1");
        categoryRepository.save(category);

        // Act: Удаление существующей категории
        categoryRepository.delete(category.getId());

        // Assert: Проверка отсутствия удаленной категории
        assertFalse(categoryRepository.existsById(category.getId())); // Категория должна быть удалена
    }

    @Test
    void testDelete_ThrowsException_WhenCategoryNotFound() {
        // Act & Assert: Проверка выброса исключения при удалении несуществующей категории
        assertThrows(CategoryNotFoundException.class, () ->
                categoryRepository.delete(999) // ID не существует
        );
    }

    @Test
    void testExistsById_ReturnsTrue_WhenExists() {
        // Arrange: Создание и сохранение категории
        Category category = new Category(0, "category-1", "Категория 1");
        categoryRepository.save(category);

        // Act & Assert: Проверка существования категории по ID
        assertTrue(categoryRepository.existsById(category.getId()));
    }

    @Test
    void testExistsById_ReturnsFalse_WhenNotExists() {
        // Act & Assert: Проверка отсутствия категории по несуществующему ID
        assertFalse(categoryRepository.existsById(999)); // ID не существует
    }
}