package com.bookmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void testBookConstructorAndGetters() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");

            assertThat(book.getId()).isEqualTo("book-1");
            assertThat(book.getTitle()).isEqualTo("1984");
            assertThat(book.getAuthor()).isEqualTo("George Orwell");
            assertThat(book.getCategoryId()).isEqualTo("cat-1");
        }

    @Test
    void testToStringReturnsTitleAndAuthor() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");

    assertThat(book.toString()).isEqualTo("1984 - George Orwell");
    }

    @Test
    void testEqualityById() {
        Book b1 = new Book("book-1", "1984", "George Orwell", "cat-1");
        Book b2 = new Book("book-1", "Different Title", "Different Author", "cat-2");

     assertThat(b1).isEqualTo(b2);
    }

    @Test
    void testInequalityWithDifferentId() {
    Book b1 = new Book("book-1", "1984", "George Orwell", "cat-1");
    Book b2 = new Book("book-2", "1984", "George Orwell", "cat-1");

        assertThat(b1).isNotEqualTo(b2);
    }
}