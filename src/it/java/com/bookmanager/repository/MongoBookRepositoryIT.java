package com.bookmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookmanager.model.Book;
import com.bookmanager.repository.mongo.MongoBookRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

class MongoBookRepositoryIT {

    private MongoClient client;
    private MongoBookRepository repository;

    @BeforeEach
    void setUp() {
        client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("bookmanager-it");
        database.getCollection("books").drop();
        repository = new MongoBookRepository(database);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void save_thenFindAll_returnsSavedBook() {
        repository.save(new Book("book-1", "1984", "George Orwell", "cat-1"));
        assertThat(repository.findAll())
                .containsExactly(new Book("book-1", "1984", "George Orwell", "cat-1"));
    }

    @Test
    void findById_whenExists_returnsBook() {
        repository.save(new Book("book-1", "1984", "George Orwell", "cat-1"));
        Book found = repository.findById("book-1");
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("1984");
        assertThat(found.getAuthor()).isEqualTo("George Orwell");
    }

    @Test
    void findById_whenNotExists_returnsNull() {
        assertThat(repository.findById("missing")).isNull();
    }

    @Test
    void delete_removesBook() {
        repository.save(new Book("book-1", "1984", "George Orwell", "cat-1"));
        repository.delete("book-1");
        assertThat(repository.findAll()).isEmpty();
    }
}