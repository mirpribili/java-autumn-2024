package ru.tbank.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void testEquals_SameObject() {
        Location location = new Location(1, "slug-1", "Город 1");
        assertEquals(location, location); // Проверка на равенство с самим собой
    }

    @Test
    void testEquals_DifferentObjectsWithSameValues() {
        Location location1 = new Location(1, "slug-1", "Город 1");
        Location location2 = new Location(1, "slug-1", "Город 1");
        assertEquals(location1, location2); // Проверка на равенство с другим объектом
    }

    @Test
    void testEquals_DifferentObjectsWithDifferentValues() {
        Location location1 = new Location(1, "slug-1", "Город 1");
        Location location2 = new Location(2, "slug-2", "Город 2");
        assertNotEquals(location1, location2); // Проверка на неравенство
    }

    @Test
    void testHashCode_SameValues() {
        Location location1 = new Location(1, "slug-1", "Город 1");
        Location location2 = new Location(1, "slug-1", "Город 1");
        assertEquals(location1.hashCode(), location2.hashCode()); // Проверка совпадения hashCode
    }

    @Test
    void testToString() {
        Location location = new Location(1, "slug-1", "Город 1");
        String expectedString = "Location(id=1, slug=slug-1, name=Город 1)";
        assertEquals(expectedString, location.toString()); // Проверка корректности toString
    }

    @Test
    void testSetSlug() {
        Location location = new Location();
        location.setSlug("new-slug");
        assertEquals("new-slug", location.getSlug()); // Проверка установки slug
    }

    @Test
    void testSetName() {
        Location location = new Location();
        location.setName("New City");
        assertEquals("New City", location.getName()); // Проверка установки имени
    }
}