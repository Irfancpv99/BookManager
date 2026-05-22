package com.bookmanager.app;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.bookmanager.controller.BookController;
import com.bookmanager.repository.mongo.MongoBookRepository;
import com.bookmanager.repository.mongo.MongoCategoryRepository;
import com.bookmanager.service.BookService;
import com.bookmanager.view.swing.BookSwingView;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import javax.swing.JFrame;

public class App {

    public static void main(String[] args) {
        String mongoUri = "mongodb://localhost:27017";
        String dbName   = "bookmanager";

        MongoClient client       = MongoClients.create(mongoUri);
        MongoDatabase database   = client.getDatabase(dbName);

        MongoBookRepository     bookRepo = new MongoBookRepository(database);
        MongoCategoryRepository catRepo  = new MongoCategoryRepository(database);

        new DatabaseInitializer(catRepo).initialize();

        BookService service = new BookService(bookRepo, catRepo);

        SwingUtilities.invokeLater(() -> {
            BookSwingView view = new BookSwingView();
            BookController controller = new BookController(service, view);
            view.setController(controller);

            controller.allBooks();
            controller.allCategories();

            JFrame frame = new JFrame("Book Manager");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.setSize(600, 450);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}