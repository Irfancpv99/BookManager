package com.bookmanager.repository;

import com.bookmanager.model.Book;

import java.util.List;

public interface BookRepository {

    List<Book> findAll();

    Book findById(String id);

    void save(Book book);

    void delete(String id);
}
