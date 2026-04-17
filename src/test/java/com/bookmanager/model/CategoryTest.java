package com.bookmanager.model;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void testCategoryConstructorAndGetters() {
    Category category = new Category("cat-1", "Fiction");

    assertThat(category.getId()).isEqualTo("cat-1");
    assertThat(category.getName()).isEqualTo("Fiction");
    }

    @Test
    void testToStringReturnsName() {
    Category category = new Category("cat-1", "Fiction");

    assertThat(category.toString()).isEqualTo("Fiction");
    }

    @Test
    void testEqualityById() {
    Category c1 = new Category("cat-1", "Fiction");
    Category c2 = new Category("cat-1", "Something Else");

    assertThat(c1).isEqualTo(c2);
    }

    @Test
    void testInequalityWithDifferentId() {
    Category c1 = new Category("cat-1", "Fiction");
    Category c2 = new Category("cat-2", "Fiction");

    assertThat(c1).isNotEqualTo(c2);
    }
}