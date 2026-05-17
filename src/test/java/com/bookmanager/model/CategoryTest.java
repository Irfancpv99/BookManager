package com.bookmanager.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void testConstructorAndGetters() {
        Category c = new Category("c1", "Fiction");
        assertThat(c.getId()).isEqualTo("c1");
        assertThat(c.getName()).isEqualTo("Fiction");
    }

    @Test
    void testSetters() {
        Category c = new Category("c1", "Fiction");
        c.setId("c2");
        c.setName("Non-Fiction");
        assertThat(c.getId()).isEqualTo("c2");
        assertThat(c.getName()).isEqualTo("Non-Fiction");
    }

    @Test
    void testToString() {
        assertThat(new Category("c1", "Fiction").toString()).isEqualTo("Fiction");
    }

    @Test
    void testEquals() {
        Category c1 = new Category("c1", "Fiction");
        Category c2 = new Category("c1", "Something Else");
        Category c3 = new Category("c2", "Fiction");

        assertThat(c1).isEqualTo(c1);          // same ref
        assertThat(c1).isNotEqualTo(null);      // null check
        assertThat(c1).isNotEqualTo("string");  // wrong class
        assertThat(c1).isEqualTo(c2);           // same id, different name
        assertThat(c1).isNotEqualTo(c3);        // different id
    }

    @Test
    void testHashCode() {
        Category c1 = new Category("c1", "Fiction");
        Category c2 = new Category("c1", "Something Else");
        Category c3 = new Category("c2", "Fiction");

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1.hashCode()).isNotEqualTo(c3.hashCode());
    }
}