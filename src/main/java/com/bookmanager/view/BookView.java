package com.bookmanager.view;

import com.bookmanager.model.Book;
import com.bookmanager.model.Category;

import java.util.List;

public interface BookView {

    void showAllBooks(List<Book> books);

    void showAllCategories(List<Category> categories);

    void bookAdded(Book book);

    void bookUpdated(Book book);

    void bookDeleted(Book book);

    void showError(String message);
}