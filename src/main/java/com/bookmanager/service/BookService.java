package com.bookmanager.service;

import com.bookmanager.model.Book;
import com.bookmanager.repository.BookRepository;
import com.bookmanager.repository.CategoryRepository;
import com.bookmanager.view.BookView;


public class BookService {

    private final BookRepository bookRepository;
	private CategoryRepository categoryRepository;
   
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
		    	       if (book.getId() != null && bookRepository.findById(book.getId()) != null) {
		    	            view.showError("Book with id " + book.getId() + " already exists");
		    	            return;
		    	        }
		    	       
		    	        bookRepository.save(book);
		    	        view.bookAdded(book);
		    	    }
    
}