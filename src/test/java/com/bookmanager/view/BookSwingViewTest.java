package com.bookmanager.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bookmanager.controller.BookController;
import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.view.swing.BookSwingView;

class BookSwingViewTest {

    private BookSwingView view;

    @Mock
    private BookController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        view = new BookSwingView();
        view.setController(controller);
    }

    // Initial state

    @Test
    void addButtonShouldBeDisabledOnStartup() {
        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    void editButtonShouldBeDisabledOnStartup() {
        assertThat(view.getEditButton().isEnabled()).isFalse();
    }

    @Test
    void deleteButtonShouldBeDisabledOnStartup() {
        assertThat(view.getDeleteButton().isEnabled()).isFalse();
    }

    // Add button enable/disable

    @Test
    void addButtonShouldEnableWhenTitleAndAuthorFilledAndNoBookSelected() {
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");

        assertThat(view.getAddButton().isEnabled()).isTrue();
    }

    @Test
    void addButtonShouldStayDisabledWhenOnlyTitleIsFilled() {
        view.getTitleField().setText("1984");
        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    void addButtonShouldStayDisabledWhenOnlyAuthorIsFilled() {
        view.getAuthorField().setText("George Orwell");
        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    void addButtonShouldDisableAgainAfterTitleIsCleared() {
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");
        view.getTitleField().setText("");

        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    // Edit button enable/disable

    @Test
    void editButtonShouldEnableWhenBookIsSelectedAndFieldsAreFilled() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        assertThat(view.getEditButton().isEnabled()).isTrue();
    }

    @Test
    void addButtonShouldDisableWhenBookIsSelected() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    // Selecting a book populates form fields

    @Test
    void selectingBookShouldPopulateFormFields() {
        Category fiction = new Category("cat-1", "Fiction");
        view.showAllCategories(Arrays.asList(fiction));
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));

        view.getBookList().setSelectedIndex(0);

        assertThat(view.getTitleField().getText()).isEqualTo("1984");
        assertThat(view.getAuthorField().getText()).isEqualTo("George Orwell");
        assertThat(view.getBookBeingEdited()).isEqualTo(book);
    }

    // Delete button enable/disable

    @Test
    void deleteButtonShouldEnableWhenBookIsSelected() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        assertThat(view.getDeleteButton().isEnabled()).isTrue();
    }

    // showAllBooks

    @Test
    void showAllBooksShouldPopulateTheList() {
        view.showAllBooks(Arrays.asList(
                new Book("book-1", "1984", "George Orwell", "cat-1"),
                new Book("book-2", "Dune", "Frank Herbert", "cat-2")));

        assertThat(view.getBookListModel().getSize()).isEqualTo(2);
    }

    @Test
    void showAllBooksWithEmptyListShouldClearTheList() {
        view.showAllBooks(Arrays.asList(new Book("book-1", "1984", "George Orwell", "cat-1")));
        view.showAllBooks(Collections.emptyList());

        assertThat(view.getBookListModel().getSize()).isEqualTo(0);
    }

    // showAllCategories

    @Test
    void showAllCategoriesShouldPopulateComboBox() {
        view.showAllCategories(Arrays.asList(
                new Category("cat-1", "Fiction"),
                new Category("cat-2", "Science")));

        assertThat(view.getCategoryCombo().getItemCount()).isEqualTo(2);
        assertThat(view.getCategoryCombo().getItemAt(0).getName()).isEqualTo("Fiction");
        assertThat(view.getCategoryCombo().getItemAt(1).getName()).isEqualTo("Science");
    }

    // bookAdded

    @Test
    void bookAddedShouldAddBookToListAndClearFields() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");

        view.bookAdded(book);

        assertThat(view.getBookListModel().getSize()).isEqualTo(1);
        assertThat(view.getTitleField().getText()).isEmpty();
        assertThat(view.getAuthorField().getText()).isEmpty();
        assertThat(view.getBookBeingEdited()).isNull();
    }

    // bookUpdated

    @Test
    void bookUpdatedShouldReplaceBookInListAndClearFields() {
        Book original = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(original));

        Book updated = new Book("book-1", "1984 - Revised", "George Orwell", "cat-1");
        view.bookUpdated(updated);

        assertThat(view.getBookListModel().getSize()).isEqualTo(1);
        assertThat(view.getBookListModel().getElementAt(0).getTitle()).isEqualTo("1984 - Revised");
        assertThat(view.getTitleField().getText()).isEmpty();
        assertThat(view.getBookBeingEdited()).isNull();
    }

    // bookDeleted

    @Test
    void bookDeletedShouldRemoveBookFromList() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));

        view.bookDeleted(book);

        assertThat(view.getBookListModel().getSize()).isEqualTo(0);
    }

    // showError

    @Test
    void showErrorShouldDisplayMessageInErrorLabel() {
        view.showError("Something went wrong");
        assertThat(view.getErrorLabel().getText()).isEqualTo("Something went wrong");
    }

    // Controller delegation

    @Test
    void clickAddButtonShouldDelegateToController() {
        Category fiction = new Category("cat-1", "Fiction");
        view.showAllCategories(Arrays.asList(fiction));
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");

        view.getAddButton().doClick();

        verify(controller).addBook(argThat(b ->
                b.getTitle().equals("1984")
                        && b.getAuthor().equals("George Orwell")
                        && b.getCategoryId().equals("cat-1")));
    }

    @Test
    void clickEditButtonShouldDelegateToController() {
        Category fiction = new Category("cat-1", "Fiction");
        view.showAllCategories(Arrays.asList(fiction));
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        view.getTitleField().setText("1984 - Revised");
        view.getEditButton().doClick();

        verify(controller).updateBook(argThat(b ->
                b.getId().equals("book-1")
                        && b.getTitle().equals("1984 - Revised")
                        && b.getAuthor().equals("George Orwell")
                        && b.getCategoryId().equals("cat-1")));
    }

    @Test
    void clickDeleteButtonShouldDelegateToController() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        view.getDeleteButton().doClick();

        verify(controller).deleteBook(book);
    }
}