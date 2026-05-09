package com.bookmanager.repository.mongo;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.bookmanager.model.Category;
import com.bookmanager.repository.CategoryRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoCategoryRepository implements CategoryRepository {

    private final MongoCollection<Document> collection;

    public MongoCategoryRepository(MongoDatabase database) {
        this.collection = database.getCollection("categories");
    }

    @Override
    public List<Category> findAll() {
        List<Category> result = new ArrayList<>();
        for (Document doc : collection.find()) {
            result.add(fromDocument(doc));
        }
        return result;
    }

    @Override
    public Category findById(String id) {
        Document doc = collection.find(eq("id", id)).first();
        if (doc == null) return null;
        return fromDocument(doc);
    }

    @Override
    public void save(Category category) {
        collection.insertOne(toDocument(category));
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(eq("id", id));
    }

    private Category fromDocument(Document doc) {
        return new Category(doc.getString("id"), doc.getString("name"));
    }

    private Document toDocument(Category category) {
        return new Document("id", category.getId())
                .append("name", category.getName());
    }
}
