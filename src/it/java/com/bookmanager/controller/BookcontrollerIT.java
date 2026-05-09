package com.bookmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.repository.mongo.MongoBookRepository;
import com.bookmanager.repository.mongo.MongoCategoryRepository;
import com.bookmanager.service.BookService;
import com.bookmanager.view.BookView;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

class BookControllerIT {

    private MongoClient client;
    private BookController controller;
    private CapturingView view;

    static class CapturingView implements BookView {
        List<Book> books;
        List<Category> categories;
        Book added;
        Book deleted;
        String error;

        @Override public void showAllBooks(List<Book> b)          { books = b; }
        @Override public void showAllCategories(List<Category> c) { categories = c; }
        @Override public void bookAdded(Book b)                   { added = b; }
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

        BookService service = new BookService(bookRepo, catRepo);
        view = new CapturingView();
        controller = new BookController(service, view);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    // allBooks

    @Test
    void allBooks_whenEmpty_passesEmptyListToView() {
        controller.allBooks();
        assertThat(view.books).isEmpty();
    }

    @Test
    void allBooks_afterAdd_returnsSavedBook() {
        controller.addBook(new Book("b-1", "1984", "George Orwell", "cat-1"));
        controller.allBooks();
        assertThat(view.books).containsExactly(new Book("b-1", "1984", "George Orwell", "cat-1"));
    }

    // allCategories

    @Test
    void allCategories_returnsSeededCategory() {
        controller.allCategories();
        assertThat(view.categories).containsExactly(new Category("cat-1", "Fiction"));
    }

    // addBook

    @Test
    void addBook_valid_notifiesViewAndPersistsToDatabase() {
        Book book = new Book("b-1", "Dune", "Frank Herbert", "cat-1");
        controller.addBook(book);

        assertThat(view.added).isEqualTo(book);

        controller.allBooks();
        assertThat(view.books).contains(book);
    }

    @Test
    void addBook_invalid_showsErrorAndDoesNotSave() {
        controller.addBook(new Book(null, "", "Author", "cat-1"));

        assertThat(view.error).isEqualTo("Title cannot be empty");
        assertThat(view.added).isNull();
    }

    // deleteBook

    @Test
    void deleteBook_existing_removesFromDatabaseAndNotifiesView() {
        Book book = new Book("b-1", "1984", "George Orwell", "cat-1");
        controller.addBook(book);

        controller.deleteBook(book);

        assertThat(view.deleted).isEqualTo(book);
        controller.allBooks();
        assertThat(view.books).doesNotContain(book);
    }

    @Test
    void deleteBook_notExisting_showsError() {
        controller.deleteBook(new Book("x-99", "Ghost", "Nobody", "cat-1"));

        assertThat(view.error).isEqualTo("Book with id x-99 no longer exists");
        assertThat(view.deleted).isNull();
    }
}