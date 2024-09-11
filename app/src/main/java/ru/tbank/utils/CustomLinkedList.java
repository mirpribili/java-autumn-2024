package ru.tbank.utils;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unchecked")
public class CustomLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private transient int size = 0;

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public T get(int index) throws InvalidIndexException {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public boolean remove(int index) throws InvalidIndexException {
        checkIndex(index);
        if (index == 0) {
            head = head.next;
            if (size == 1) {
                tail = null;
            }
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            current.next = current.next.next;
            if (index == size - 1) {
                tail = current;
            }
        }
        size--;
        return true;
    }

    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (data == null ? current.data == null : data.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void addAll(CustomLinkedList<? extends T> list) {
        if (list == null) {
            throw new NullPointerException("The specified collection is null");
        }
        if (list.head != null) {
            if (head == null) {
                head = (Node<T>) list.head;
                tail = (Node<T>) list.tail;
            } else {
                Node<? extends T> current = list.head;
                while (current != null) {
                    add(current.data);
                    current = current.next;
                }
            }
            size += list.size;
        }
    }

    public Stream<T> stream() {
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<T>(size, Spliterator.ORDERED | Spliterator.SIZED) {
                    Node<T> current = head;

                    @Override
                    public boolean tryAdvance(java.util.function.Consumer<? super T> action) {
                        if (current == null) {
                            return false;
                        }
                        action.accept(current.data);
                        current = current.next;
                        return true;
                    }
                },
                false
        );
    }

    public int size() {
        return size;
    }

    private void checkIndex(int index) throws InvalidIndexException {
        if (index < 0 || index >= size) {
            throw new InvalidIndexException("Ожидается по индексу: " + index + ", Размер: " + size);
        }
    }
}
