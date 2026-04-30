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

public class BookSwingViewTest {

    private BookSwingView view;

    @Mock
    private BookController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        view = new BookSwingView();
        view.setController(controller);
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    public void addButtonShouldBeDisabledOnStartup() {
        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    public void deleteButtonShouldBeDisabledOnStartup() {
        assertThat(view.getDeleteButton().isEnabled()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Add button enable/disable
    // -------------------------------------------------------------------------

    @Test
    public void addButtonShouldEnableWhenTitleAndAuthorAreBothFilled() {
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");

        assertThat(view.getAddButton().isEnabled()).isTrue();
    }

    @Test
    public void addButtonShouldStayDisabledWhenOnlyTitleIsFilled() {
        view.getTitleField().setText("1984");

        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    public void addButtonShouldStayDisabledWhenOnlyAuthorIsFilled() {
        view.getAuthorField().setText("George Orwell");

        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    @Test
    public void addButtonShouldDisableAgainAfterTitleIsCleared() {
        view.getTitleField().setText("1984");
        view.getAuthorField().setText("George Orwell");
        view.getTitleField().setText("");

        assertThat(view.getAddButton().isEnabled()).isFalse();
    }

    // -------------------------------------------------------------------------
    // Delete button enable/disable
    // -------------------------------------------------------------------------

    @Test
    public void deleteButtonShouldEnableWhenBookIsSelected() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));

        view.getBookList().setSelectedIndex(0);

        assertThat(view.getDeleteButton().isEnabled()).isTrue();
    }

    // -------------------------------------------------------------------------
    // showAllBooks
    // -------------------------------------------------------------------------

    @Test
    public void showAllBooksShouldPopulateTheList() {
        Book b1 = new Book("book-1", "1984", "George Orwell", "cat-1");
        Book b2 = new Book("book-2", "Dune", "Frank Herbert", "cat-2");

        view.showAllBooks(Arrays.asList(b1, b2));

        assertThat(view.getBookListModel().getSize()).isEqualTo(2);
    }

    @Test
    public void showAllBooksWithEmptyListShouldClearTheList() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.showAllBooks(Collections.emptyList());

        assertThat(view.getBookListModel().getSize()).isEqualTo(0);
    }

    // -------------------------------------------------------------------------
    // showAllCategories
    // -------------------------------------------------------------------------

    @Test
    public void showAllCategoriesShouldPopulateComboBox() {
        Category c1 = new Category("cat-1", "Fiction");
        Category c2 = new Category("cat-2", "Science");

        view.showAllCategories(Arrays.asList(c1, c2));

        assertThat(view.getCategoryCombo().getItemCount()).isEqualTo(2);
        assertThat(view.getCategoryCombo().getItemAt(0).getName()).isEqualTo("Fiction");
        assertThat(view.getCategoryCombo().getItemAt(1).getName()).isEqualTo("Science");
    }

    // -------------------------------------------------------------------------
    // bookAdded
    // -------------------------------------------------------------------------

    @Test
    public void bookAddedShouldAddBookToListAndClearFields() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");

        view.bookAdded(book);

        assertThat(view.getBookListModel().getSize()).isEqualTo(1);
        assertThat(view.getTitleField().getText()).isEmpty();
        assertThat(view.getAuthorField().getText()).isEmpty();
    }

    // -------------------------------------------------------------------------
    // bookDeleted
    // -------------------------------------------------------------------------

    @Test
    public void bookDeletedShouldRemoveBookFromList() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));

        view.bookDeleted(book);

        assertThat(view.getBookListModel().getSize()).isEqualTo(0);
    }

    // -------------------------------------------------------------------------
    // showError
    // -------------------------------------------------------------------------

    @Test
    public void showErrorShouldDisplayMessageInErrorLabel() {
        view.showError("Something went wrong");

        assertThat(view.getErrorLabel().getText()).isEqualTo("Something went wrong");
    }

    // -------------------------------------------------------------------------
    // Controller delegation
    // -------------------------------------------------------------------------

    @Test
    public void clickAddButtonShouldDelegateToController() {
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
    public void clickDeleteButtonShouldDelegateToController() {
        Book book = new Book("book-1", "1984", "George Orwell", "cat-1");
        view.showAllBooks(Arrays.asList(book));
        view.getBookList().setSelectedIndex(0);

        view.getDeleteButton().doClick();

        verify(controller).deleteBook(book);
    }
}