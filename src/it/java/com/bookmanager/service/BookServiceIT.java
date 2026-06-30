package com.bookmanager.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.repository.mongo.MongoBookRepository;
import com.bookmanager.repository.mongo.MongoCategoryRepository;
import com.bookmanager.view.BookView;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

class BookServiceIT {

    private MongoClient client;
    private BookService service;
    private CapturingView view;

    static class CapturingView implements BookView {
        List<Book> books;
        List<Category> categories;
        Book added;
        Book updated;
        Book deleted;
        String error;

        @Override public void showAllBooks(List<Book> b)          { books = b; }
        @Override public void showAllCategories(List<Category> c) { categories = c; }
        @Override public void bookAdded(Book b)                   { added = b; }
        @Override public void bookUpdated(Book b)                 { updated = b; }
        @Override public void bookDeleted(Book b)                 { deleted = b; }
        @Override public void showError(String msg)               { error = msg; }
    }

    @BeforeEach
    void setUp() {
        client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("bookmanager-it");
        database.getCollection("books").drop();
        database.getCollection("categories").drop();

        MongoBookRepository bookRepo = new MongoBookRepository(database);
        MongoCategoryRepository catRepo = new MongoCategoryRepository(database);
        catRepo.save(new Category("cat-1", "Fiction"));

        service = new BookService(bookRepo, catRepo);
        view = new CapturingView();
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void allBooks_returnsPersistedBooks() {
        service.allBooks(view);
        assertThat(view.books).isEmpty();

        service.addBook(new Book("b-1", "1984", "George Orwell", "cat-1"), view);
        service.allBooks(view);
        assertThat(view.books).containsExactly(new Book("b-1", "1984", "George Orwell", "cat-1"));
    }

    @Test
    void allCategories_returnsSeededCategory() {
        service.allCategories(view);
        assertThat(view.categories).containsExactly(new Category("cat-1", "Fiction"));
    }

    @Test
    void addBook_nullId_persistsAndNotifiesView() {
        Book book = new Book(null, "Dune", "Frank Herbert", "cat-1");
        service.addBook(book, view);
        assertThat(view.added).isEqualTo(book);
        service.allBooks(view);
        assertThat(view.books).contains(book);
    }

    @Test
    void updateBook_valid_updatesAndNotifiesView() {
        service.addBook(new Book("b-1", "1984", "George Orwell", "cat-1"), view);
        service.updateBook(new Book("b-1", "1984 - Revised", "George Orwell", "cat-1"), view);
        assertThat(view.updated.getTitle()).isEqualTo("1984 - Revised");
        service.allBooks(view);
        assertThat(view.books.get(0).getTitle()).isEqualTo("1984 - Revised");
    }

    @Test
    void deleteBook_existing_removesAndNotifiesView() {
        Book book = new Book("b-1", "1984", "George Orwell", "cat-1");
        service.addBook(book, view);
        service.deleteBook(book, view);
        assertThat(view.deleted).isEqualTo(book);
        service.allBooks(view);
        assertThat(view.books).doesNotContain(book);
    }
}