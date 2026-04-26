package com.bookmanager.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookmanager.model.Category;
import com.bookmanager.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DatabaseInitializer databaseInitializer;

    @Test
    void initialize_whenCategoriesEmpty_shouldSeedDefaultCategories() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        databaseInitializer.initialize();

        verify(categoryRepository, times(5)).save(any(Category.class));
    }

    @Test
    void initialize_whenCategoriesAlreadyExist_shouldNotSeedAgain() {
        when(categoryRepository.findAll()).thenReturn(
                Arrays.asList(new Category("cat-1", "Fiction")));

        databaseInitializer.initialize();

        verify(categoryRepository, never()).save(any());
    }
}
