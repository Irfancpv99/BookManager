package com.bookmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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

    @Mock private BookRepository bookRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BookView bookView;

    @InjectMocks
    private BookService bookService;

    private Book book1;

    @BeforeEach
    void setUp() {
        book1 = new Book("book-1", "1984", "George Orwell", "cat-1");
    }

    @Test
    void allBooks_fetchesAndPassesToView() {
        List<Book> books = Arrays.asList(book1);
        when(bookRepository.findAll()).thenReturn(books);
        bookService.allBooks(bookView);
        verify(bookView).showAllBooks(books);
    }

    @Test
    void allCategories_fetchesAndPassesToView() {
        List<Category> cats = Arrays.asList(new Category("cat-1", "Fiction"));
        when(categoryRepository.findAll()).thenReturn(cats);
        bookService.allCategories(bookView);
        verify(bookView).showAllCategories(cats);
    }

    // addBook

    @Test
    void addBook_nullId_savesAndNotifiesView() {
        Book b = new Book(null, "Dune", "Frank Herbert", "cat-1");
        bookService.addBook(b, bookView);
        verify(bookRepository, never()).findById(any());
        verify(bookRepository).save(b);
        verify(bookView).bookAdded(b);
    }

    @Test
    void addBook_nonExistingId_savesAndNotifiesView() {
        when(bookRepository.findById("book-2")).thenReturn(null);
        Book b = new Book("book-2", "Dune", "Frank Herbert", "cat-1");
        bookService.addBook(b, bookView);
        verify(bookRepository).save(b);
        verify(bookView).bookAdded(b);
    }

    @Test
    void addBook_duplicateId_showsError() {
        when(bookRepository.findById("book-1")).thenReturn(book1);
        bookService.addBook(new Book("book-1", "1984", "George Orwell", "cat-1"), bookView);
        verify(bookView).showError("Book with id book-1 already exists");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_invalidTitle_showsError() {
        bookService.addBook(new Book(null, null, "Author", "cat-1"), bookView);
        verify(bookView).showError("Title cannot be empty");
        bookService.addBook(new Book(null, "", "Author", "cat-1"), bookView);
        verify(bookView, times(2)).showError("Title cannot be empty");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_invalidAuthor_showsError() {
        bookService.addBook(new Book(null, "Title", null, "cat-1"), bookView);
        verify(bookView).showError("Author cannot be empty");
        bookService.addBook(new Book(null, "Title", "", "cat-1"), bookView);
        verify(bookView, times(2)).showError("Author cannot be empty");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void addBook_nullCategory_showsError() {
        bookService.addBook(new Book(null, "Title", "Author", null), bookView);
        verify(bookView).showError("Category must be selected");
        verify(bookRepository, never()).save(any());
    }

    // updateBook

    @Test
    void updateBook_valid_updatesAndNotifiesView() {
        when(bookRepository.findById("book-1")).thenReturn(book1);
        Book updated = new Book("book-1", "1984 Updated", "George Orwell", "cat-1");
        bookService.updateBook(updated, bookView);
        verify(bookRepository).update(updated);
        verify(bookView).bookUpdated(updated);
    }

    @Test
    void updateBook_notExists_showsError() {
        when(bookRepository.findById("book-1")).thenReturn(null);
        bookService.updateBook(book1, bookView);
        verify(bookView).showError("Book with id book-1 no longer exists");
        verify(bookRepository, never()).update(any());
    }

    @Test
    void updateBook_invalidTitle_showsError() {
        bookService.updateBook(new Book("book-1", null, "Author", "cat-1"), bookView);
        verify(bookView).showError("Title cannot be empty");
        bookService.updateBook(new Book("book-1", "", "Author", "cat-1"), bookView);
        verify(bookView, times(2)).showError("Title cannot be empty");
        verify(bookRepository, never()).update(any());
    }

    @Test
    void updateBook_invalidAuthor_showsError() {
        bookService.updateBook(new Book("book-1", "Title", null, "cat-1"), bookView);
        verify(bookView).showError("Author cannot be empty");
        bookService.updateBook(new Book("book-1", "Title", "", "cat-1"), bookView);
        verify(bookView, times(2)).showError("Author cannot be empty");
        verify(bookRepository, never()).update(any());
    }

    @Test
    void updateBook_nullCategory_showsError() {
        bookService.updateBook(new Book("book-1", "1984", "George Orwell", null), bookView);
        verify(bookView).showError("Category must be selected");
        verify(bookRepository, never()).update(any());
    }

    // deleteBook

    @Test
    void deleteBook_exists_deletesAndNotifiesView() {
        when(bookRepository.findById("book-1")).thenReturn(book1);
        bookService.deleteBook(book1, bookView);
        verify(bookRepository).delete("book-1");
        verify(bookView).bookDeleted(book1);
    }

    @Test
    void deleteBook_notExists_showsError() {
        when(bookRepository.findById("book-1")).thenReturn(null);
        bookService.deleteBook(book1, bookView);
        verify(bookView).showError("Book with id book-1 no longer exists");
        verify(bookRepository, never()).delete(any());
    }
}