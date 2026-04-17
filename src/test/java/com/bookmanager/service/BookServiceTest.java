package com.bookmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.repository.*;
import com.bookmanager.view.BookView;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookView bookView;

    @InjectMocks
    private BookService bookService;

    private Category fiction;
    private Book book1;

    @BeforeEach
    void setUp() {
        fiction = new Category("cat-1", "Fiction");
        book1 = new Book("book-1", "1984", "George Orwell", "cat-1");
    }

    // ------------------------------
    	// allBooks
    // ----------------------------

    @Test
    void allBooks_shouldFetchAllAndPassToView() {
        List<Book> books = Arrays.asList(book1);
        when(bookRepository.findAll()).thenReturn(books);

        bookService.allBooks(bookView);

        verify(bookView).showAllBooks(books);
    }
    
    @Test
    void allBooks_whenEmpty_shouldPassEmptyListToView() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        bookService.allBooks(bookView);

        verify(bookView).showAllBooks(Collections.emptyList());
    }
    
    @Test
    void addBook_whenValid_shouldSaveAndNotifyView() {
        Book newBook = new Book(null, "Brave New World", "Aldous Huxley", "cat-1");

        bookService.addBook(newBook, bookView);

        verify(bookRepository).save(newBook);
        verify(bookView).bookAdded(newBook);
    }
    
    // ------------------------------------
    	// allCategories
    // ------------------------------

    @Test
    void allCategories_shouldFetchAllAndPassToView() {
        List<Category> categories = Arrays.asList(fiction);
        when(categoryRepository.findAll()).thenReturn(categories);

        bookService.allCategories(bookView);

        verify(bookView).showAllCategories(categories);
    }
    
    @Test
    void addBook_whenTitleIsEmpty_shouldShowErrorAndNotSave() {
        Book bad = new Book(null, "", "Aldous Huxley", "cat-1");

        bookService.addBook(bad, bookView);

        verify(bookView).showError("Title cannot be empty");
        verify(bookRepository, never()).save(any());
    }
	    @Test
	    void addBook_whenAuthorIsEmpty_shouldShowErrorAndNotSave() {
	        Book bad = new Book(null, "Brave New World", "", "cat-1");
	
	        bookService.addBook(bad, bookView);
	
	        verify(bookView).showError("Author cannot be empty");
	        verify(bookRepository, never()).save(any());
	    }
	   
	    
	    @Test
	    void addBook_whenCategoryIdIsNull_shouldShowErrorAndNotSave() {
	        Book bad = new Book(null, "Brave New World", "Aldous Huxley", null);

	        bookService.addBook(bad, bookView);

	        verify(bookView).showError("Category must be selected");
	        verify(bookRepository, never()).save(any());
	    }
	    
	    
	    @Test
	    void addBook_whenDuplicateId_shouldShowErrorAndNotSave() {
	        when(bookRepository.findById("book-1")).thenReturn(book1);
	        Book duplicate = new Book("book-1", "1984", "George Orwell", "cat-1");

	        bookService.addBook(duplicate, bookView);

	        verify(bookView).showError("Book with id book-1 already exists");
	        verify(bookRepository, never()).save(any());
	    } 

	    
	 // ------------------------------
	    // deleteBook
	    // ---------------------

	    @Test
	    void deleteBook_whenExists_shouldDeleteAndNotifyView() {
	        when(bookRepository.findById("book-1")).thenReturn(book1);

	        bookService.deleteBook(book1, bookView);

	        verify(bookRepository).delete("book-1");
	        verify(bookView).bookDeleted(book1);
	    }
}