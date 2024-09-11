package ru.tbank;

import org.junit.Test;
import ru.tbank.utils.CustomLinkedList;
import ru.tbank.utils.InvalidIndexException;

import static org.junit.Assert.*;

public class CustomLinkedListTest {
    private CustomLinkedList<String> list;

    @Test
    public void testGetThrowsExceptionForInvalidIndex() {
        list = new CustomLinkedList<>();
        list.add("first");
        list.add("second");

        InvalidIndexException thrown = assertThrows(InvalidIndexException.class, () -> list.get(-1));
        assertEquals("Ожидается по индексу: -1, Размер: 2", thrown.getMessage());

        thrown = assertThrows(InvalidIndexException.class, () -> list.get(2));
        assertEquals("Ожидается по индексу: 2, Размер: 2", thrown.getMessage());
    }

    @Test
    public void testRemoveThrowsExceptionForInvalidIndex() {
        list = new CustomLinkedList<>();
        list.add("first");
        list.add("second");

        InvalidIndexException thrown = assertThrows(InvalidIndexException.class, () -> list.remove(-1));
        assertEquals("Ожидается по индексу: -1, Размер: 2", thrown.getMessage());

        thrown = assertThrows(InvalidIndexException.class, () -> list.remove(2));
        assertEquals("Ожидается по индексу: 2, Размер: 2", thrown.getMessage());
    }

    @Test
    public void testAddAllThrowsExceptionForNullList() {
        list = new CustomLinkedList<>();
        list.add("first");

        NullPointerException thrown = assertThrows(NullPointerException.class, () -> list.addAll(null));
        assertEquals("The specified collection is null", thrown.getMessage());
    }
}
