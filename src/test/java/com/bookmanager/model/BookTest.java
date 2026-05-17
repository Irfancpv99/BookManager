package com.bookmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void testConstructorAndGetters() {
        Book b = new Book("b1", "1984", "George Orwell", "c1");
        assertThat(b.getId()).isEqualTo("b1");
        assertThat(b.getTitle()).isEqualTo("1984");
        assertThat(b.getAuthor()).isEqualTo("George Orwell");
        assertThat(b.getCategoryId()).isEqualTo("c1");
    }

    @Test
    void testSetters() {
        Book b = new Book("b1", "1984", "George Orwell", "c1");
        b.setId("b2");
        b.setTitle("Dune");
        b.setAuthor("Frank Herbert");
        b.setCategoryId("c2");
        assertThat(b.getId()).isEqualTo("b2");
        assertThat(b.getTitle()).isEqualTo("Dune");
        assertThat(b.getAuthor()).isEqualTo("Frank Herbert");
        assertThat(b.getCategoryId()).isEqualTo("c2");
    }

    @Test
    void testToString() {
        assertThat(new Book("b1", "1984", "George Orwell", "c1").toString())
                .isEqualTo("1984 - George Orwell");
    }

    @Test
    void testEquals() {
        Book b1 = new Book("b1", "1984", "George Orwell", "c1");
        Book b2 = new Book("b1", "Dune", "Frank Herbert", "c2");
        Book b3 = new Book("b2", "1984", "George Orwell", "c1");

        assertThat(b1).isEqualTo(b1);          // same ref
        assertThat(b1).isNotEqualTo(null);      // null check
        assertThat(b1).isNotEqualTo("string");  // wrong class
        assertThat(b1).isEqualTo(b2);           // same id, different fields
        assertThat(b1).isNotEqualTo(b3);        // different id
    }

    @Test
    void testHashCode() {
        Book b1 = new Book("b1", "1984", "George Orwell", "c1");
        Book b2 = new Book("b1", "Dune", "Frank Herbert", "c2");
        Book b3 = new Book("b2", "1984", "George Orwell", "c1");

        assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        assertThat(b1.hashCode()).isNotEqualTo(b3.hashCode());
    }
}