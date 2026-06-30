package com.bookmanager.service;

import java.util.UUID;

import com.bookmanager.model.Book;
import com.bookmanager.repository.BookRepository;
import com.bookmanager.repository.CategoryRepository;
import com.bookmanager.view.BookView;

public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    
    private static final String BOOK_WITH_ID = "Book with id ";
     private static final String BOOK_EXIST = " already exists";
     	private static final String NOT_EXIST = " no longer exists";

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public void allBooks(BookView view) {
        view.showAllBooks(bookRepository.findAll());
    }

    public void allCategories(BookView view) {
        view.showAllCategories(categoryRepository.findAll());
    }

    public void addBook(Book book, BookView view) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            view.showError("Title cannot be empty");
            return;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            view.showError("Author cannot be empty");
            return;
        }
        if (book.getCategoryId() == null) {
            view.showError("Category must be selected");
            return;
        }
        if (book.getId() == null ) {
            book.setId(UUID.randomUUID().toString());
            bookRepository.save(book);
            view.bookAdded(book);
            return;
        }

        if (bookRepository.findById(book.getId()) != null) {
            view.showError(BOOK_WITH_ID + book.getId() + BOOK_EXIST);
            return;
        }

        bookRepository.save(book);
        view.bookAdded(book);
    }

    public void updateBook(Book book, BookView view) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            view.showError("Title cannot be empty");
            return;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            view.showError("Author cannot be empty");
            return;
        }
        if (book.getCategoryId() == null) {
            view.showError("Category must be selected");
            return;
        }
        if (bookRepository.findById(book.getId()) == null) {
            view.showError( BOOK_WITH_ID + book.getId() + NOT_EXIST);
            return;
        }
        bookRepository.update(book);
        view.bookUpdated(book);
    }

    public void deleteBook(Book book, BookView view) {
        if (bookRepository.findById(book.getId()) == null) {
            view.showError(BOOK_WITH_ID + book.getId() + NOT_EXIST);
            return;
        }
        bookRepository.delete(book.getId());
        view.bookDeleted(book);
    }
}