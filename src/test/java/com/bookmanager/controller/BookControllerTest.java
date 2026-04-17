package com.bookmanager.controller;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookmanager.model.Book;
import com.bookmanager.service.BookService;
import com.bookmanager.view.BookView;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookView bookView;

    @InjectMocks
    private BookController bookController;

    private Book book1;

    @BeforeEach
    void setUp() {
        book1 = new Book("book-1", "1984", "George Orwell", "cat-1");
    }

    @Test
    void shouldShowAllBooks() {
        bookController.allBooks();

        verify(bookService).allBooks(bookView);
    }
    @Test
    void shouldShowAllCategories() {
        bookController.allCategories();
 
        verify(bookService).allCategories(bookView);
    }
    
    @Test
    void shouldSaveNewBook() {
        bookController.addBook(book1);
 
        verify(bookService).addBook(book1, bookView);
    }
    
    @Test
    void shouldRemoveBook() {
        bookController.deleteBook(book1);
 
        verify(bookService).deleteBook(book1, bookView);
    }
}