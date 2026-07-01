package com.bookmanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookmanager.model.Category;
import com.bookmanager.repository.mongo.MongoCategoryRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

class MongoCategoryRepositoryIT {

    private MongoClient client;
    private MongoCategoryRepository repository;

    @BeforeEach
    void setUp() {
        client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("bookmanager-it");
        database.getCollection("categories").drop();
        repository = new MongoCategoryRepository(database);
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
    void save_thenFindAll_returnsSavedCategory() {
        repository.save(new Category("cat-1", "Fiction"));

        assertThat(repository.findAll()).containsExactly(new Category("cat-1", "Fiction"));
    }

    @Test
    void findById_whenExists_returnsCategory() {
        repository.save(new Category("cat-1", "Fiction"));

        Category found = repository.findById("cat-1");

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Fiction");
    }

    @Test
    void findById_whenNotExists_returnsNull() {
        assertThat(repository.findById("missing")).isNull();
    }

    @Test
    void delete_removesCategory() {
        repository.save(new Category("cat-1", "Fiction"));

        repository.delete("cat-1");

        assertThat(repository.findAll()).isEmpty();
    }
}
