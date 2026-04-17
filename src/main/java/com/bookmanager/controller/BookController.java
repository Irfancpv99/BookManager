package com.bookmanager.controller;

import com.bookmanager.model.Book;
import com.bookmanager.service.BookService;
import com.bookmanager.view.BookView;


public class BookController {
	
	private final BookService bookService;
    private final BookView bookView;
	
	public BookController(BookService bookService, BookView bookView) {
        this.bookService = bookService;
        this.bookView = bookView;
    }
	
	public void allBooks() {
        bookService.allBooks(bookView);
    }
	public void allCategories() {
        bookService.allCategories(bookView);
    }
	 
		public void addBook(Book book) {
	        bookService.addBook(book, bookView);
	    }
		
		 public void deleteBook(Book book) {
		        bookService.deleteBook(book, bookView);
		    }
} 