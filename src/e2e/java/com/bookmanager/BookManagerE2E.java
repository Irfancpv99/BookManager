package com.bookmanager;

import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.bookmanager.app.DatabaseInitializer;
import com.bookmanager.controller.BookController;
import com.bookmanager.repository.mongo.MongoBookRepository;
import com.bookmanager.repository.mongo.MongoCategoryRepository;
import com.bookmanager.service.BookService;
import com.bookmanager.view.swing.BookSwingView;

class BookManagerE2E {

    private static final String DB_NAME = "bookmanager_e2e";

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<org.bson.Document> books;

    private FrameFixture window;

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        client = MongoClients.create("mongodb://localhost:27017");
        database = client.getDatabase(DB_NAME);
        books = database.getCollection("books");

        database.getCollection("books").drop();
        database.getCollection("categories").drop();

        MongoBookRepository bookRepo = new MongoBookRepository(database);
        MongoCategoryRepository catRepo = new MongoCategoryRepository(database);
        new DatabaseInitializer(catRepo).initialize();

        BookService service = new BookService(bookRepo, catRepo);

        JFrame frame = GuiActionRunner.execute(() -> {
            BookSwingView view = new BookSwingView();
            BookController controller = new BookController(service, view);
            view.setController(controller);
            controller.allBooks();
            controller.allCategories();

            JFrame f = new JFrame();
            f.setContentPane(view);
            f.pack();
            f.setVisible(true);
            return f;
        });

        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
        client.close();
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    @GUITest
    void onStartup_listIsEmptyAndButtonsAreDisabled() {
        window.list("bookList").requireItemCount(0);
        window.button("addButton").requireDisabled();
        window.button("editButton").requireDisabled();
        window.button("deleteButton").requireDisabled();
    }

    @Test
    @GUITest
    void onStartup_categoriesAreLoaded() {
        window.comboBox("categoryCombo").requireItemCount(1);
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Test
    @GUITest
    void addBook_appearsInListAndFieldsAreCleared() {
        window.textBox("titleField").enterText("1984");
        window.textBox("authorField").enterText("George Orwell");
        window.button("addButton").click();

        window.list("bookList").requireItemCount(1);
        window.textBox("titleField").requireText("");
        window.textBox("authorField").requireText("");
        window.label("errorLabel").requireText("");
    }

    @Test
    @GUITest
    void addBook_persistsAcrossReload() {
        window.textBox("titleField").enterText("Dune");
        window.textBox("authorField").enterText("Frank Herbert");
        window.button("addButton").click();

        window.list("bookList").requireItemCount(1);
        assertThat(books.countDocuments()).isEqualTo(1);

        org.bson.Document saved = books.find().first();
        assertThat(saved.getString("title")).isEqualTo("Dune");
    }

    // ── Read (select populates form) ──────────────────────────────────────────

    @Test
    @GUITest
    void selectingBook_populatesFormAndTogglesButtons() {
        window.textBox("titleField").enterText("1984");
        window.textBox("authorField").enterText("George Orwell");
        window.button("addButton").click();

        window.list("bookList").selectItem(0);

        window.textBox("titleField").requireText("1984");
        window.textBox("authorField").requireText("George Orwell");
        window.button("editButton").requireEnabled();
        window.button("deleteButton").requireEnabled();
        window.button("addButton").requireDisabled();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Test
    @GUITest
    void editBook_updatesListAndDatabase() {
        window.textBox("titleField").enterText("1984");
        window.textBox("authorField").enterText("George Orwell");
        window.button("addButton").click();

        window.list("bookList").selectItem(0);
        window.textBox("titleField").deleteText();
        window.textBox("titleField").enterText("1984 - Revised");
        window.button("editButton").click();

        window.list("bookList").requireItemCount(1);
        window.textBox("titleField").requireText("");
        window.label("errorLabel").requireText("");

        org.bson.Document updated = books.find().first();
        assertThat(updated.getString("title")).isEqualTo("1984 - Revised");
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Test
    @GUITest
    void deleteBook_removesFromListAndDatabase() {
        window.textBox("titleField").enterText("1984");
        window.textBox("authorField").enterText("George Orwell");
        window.button("addButton").click();

        window.list("bookList").selectItem(0);
        window.button("deleteButton").click();

        window.list("bookList").requireItemCount(0);
        assertThat(books.countDocuments()).isZero();
    }
}