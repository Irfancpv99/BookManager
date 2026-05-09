package com.bookmanager.repository.mongo;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.bookmanager.model.Book;
import com.bookmanager.repository.BookRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoBookRepository implements BookRepository {

    private final MongoCollection<Document> collection;

    public MongoBookRepository(MongoDatabase database) {
        this.collection = database.getCollection("books");
    }

    @Override
    public List<Book> findAll() {
        List<Book> result = new ArrayList<>();
        for (Document doc : collection.find()) {
            result.add(fromDocument(doc));
        }
        return result;
    }

    @Override
    public Book findById(String id) {
        Document doc = collection.find(eq("id", id)).first();
        if (doc == null) return null;
        return fromDocument(doc);
    }

    @Override
    public void save(Book book) {
        collection.insertOne(toDocument(book));
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(eq("id", id));
    }

    private Book fromDocument(Document doc) {
        return new Book(
                doc.getString("id"),
                doc.getString("title"),
                doc.getString("author"),
                doc.getString("categoryId"));
    }

    private Document toDocument(Book book) {
        return new Document("id", book.getId())
                .append("title", book.getTitle())
                .append("author", book.getAuthor())
                .append("categoryId", book.getCategoryId());
    }
}
