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

        BookService service = new BookService(bookRepo, catRepo);
        view = new CapturingView();
        controller = new BookController(service, view);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void addBook_persistsThroughTheFullStackAndNotifiesView() {
        Book book = new Book("b-1", "Dune", "Frank Herbert", "cat-1");
        controller.addBook(book);

        assertThat(view.added).isEqualTo(book);
        controller.allBooks();
        assertThat(view.books).containsExactly(book);
    }

    @Test
    void allCategories_returnsSeededCategory() {
        controller.allCategories();
        assertThat(view.categories).containsExactly(new Category("cat-1", "Fiction"));
    }
}
