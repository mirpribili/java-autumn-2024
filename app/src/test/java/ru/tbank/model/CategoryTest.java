package ru.tbank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testEquals_SameObject() {
        Category category = new Category(1, "slug-1", "Категория 1");
        assertEquals(category, category); // Проверка на равенство с самим собой
    }

    @Test
    void testEquals_DifferentObjectsWithSameValues() {
        Category category1 = new Category(1, "slug-1", "Категория 1");
        Category category2 = new Category(1, "slug-1", "Категория 1");
        assertEquals(category1, category2); // Проверка на равенство с другим объектом
    }

    @Test
    void testEquals_DifferentObjectsWithDifferentValues() {
        Category category1 = new Category(1, "slug-1", "Категория 1");
        Category category2 = new Category(2, "slug-2", "Категория 2");
        assertNotEquals(category1, category2); // Проверка на неравенство
    }

    @Test
    void testHashCode_SameValues() {
        Category category1 = new Category(1, "slug-1", "Категория 1");
        Category category2 = new Category(1, "slug-1", "Категория 1");
        assertEquals(category1.hashCode(), category2.hashCode()); // Проверка совпадения hashCode
    }

    @Test
    void testToString() {
        Category category = new Category(1, "slug-1", "Категория 1");
        String expectedString = "Category(id=1, slug=slug-1, name=Категория 1)";
        assertEquals(expectedString, category.toString()); // Проверка корректности toString
    }

    @Test
    void testSetSlug() {
        Category category = new Category();
        category.setSlug("new-slug");
        assertEquals("new-slug", category.getSlug()); // Проверка установки slug
    }

    @Test
    void testSetName() {
        Category category = new Category();
        category.setName("New Category");
        assertEquals("New Category", category.getName()); // Проверка установки имени
    }
}