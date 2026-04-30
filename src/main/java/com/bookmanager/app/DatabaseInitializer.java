package com.bookmanager.app;

import com.bookmanager.model.Category;
import com.bookmanager.repository.CategoryRepository;

import java.util.Arrays;
import java.util.List;

public class DatabaseInitializer {

    private final CategoryRepository categoryRepository;

    public DatabaseInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void initialize() {
        if (!categoryRepository.findAll().isEmpty()) {
            return;
        }
        List<Category> defaults = Arrays.asList(
                new Category("cat-1", "Fiction"),
                new Category("cat-2", "Non-Fiction"),
                new Category("cat-3", "Science"),
                new Category("cat-4", "History"),
                new Category("cat-5", "Technology"));
        defaults.forEach(categoryRepository::save);
    }
}
