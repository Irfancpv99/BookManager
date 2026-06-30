package com.bookmanager.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bookmanager.controller.BookController;
import com.bookmanager.model.Book;
import com.bookmanager.model.Category;

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

    @Test
    void shouldStartWithButtonsDisabled() {
        assertThat(view.getAddButton().isEnabled()).isFalse();
        assertThat(view.getEditButton().isEnabled()).isFalse();
        assertThat(view.getDeleteButton().isEnabled()).isFalse();

        view.onDeleteBook();

        verify(controller, never()).deleteBook(any());
    }

    @Test
    void shouldEnableAddButtonWhenFieldsAreFilled() {
        fillForm("1984", "George Orwell");

        assertThat(view.getAddButton().isEnabled()).isTrue();
        assertThat(view.getEditButton().isEnabled()).isFalse();

        view.getAddButton().doClick();

        verify(controller).addBook(argThat(book ->
                book.getCategoryId() == null
                        && book.getTitle().equals("1984")));
    }

    @Test
    void shouldAddBookWithSelectedCategory() {
        addCategories(category("cat-1", "Fiction"));

        fillForm("1984", "George Orwell");

        view.getAddButton().doClick();

        verify(controller).addBook(argThat(book ->
                book.getCategoryId().equals("cat-1")));
    }

    @Test
    void shouldPopulateFormWhenBookSelected() {
        addCategories(category("cat-1", "Fiction"));

        Book book = book("book-1", "1984", "George Orwell", "cat-1");

        view.showAllBooks(List.of(book));

        selectFirstBook();

        assertThat(view.getTitleField().getText()).isEqualTo("1984");
        assertThat(view.getAuthorField().getText()).isEqualTo("George Orwell");
        assertThat(view.getBookBeingEdited()).isEqualTo(book);

        assertThat(view.getAddButton().isEnabled()).isFalse();
        assertThat(view.getEditButton().isEnabled()).isTrue();
        assertThat(view.getDeleteButton().isEnabled()).isTrue();
    }

    @Test
    void shouldEditSelectedBook() {
        addCategories(category("cat-1", "Fiction"));

        Book original = book("book-1", "1984", "George Orwell", "cat-1");

        view.showAllBooks(List.of(original));

        selectFirstBook();

        view.getTitleField().setText("1984 Revised");

        view.getEditButton().doClick();

        verify(controller).updateBook(argThat(book ->
                book.getId().equals("book-1")
                        && book.getTitle().equals("1984 Revised")));
    }

    @Test
    void shouldIgnoreEditWhenNothingSelected() {
        fillForm("Title", "Author");

        view.onEditBook();

        verify(controller, never()).updateBook(any());
    }

    @Test
    void shouldEditWithNullCategory() {
        view.showAllBooks(List.of(
                book("book-1", "1984", "George Orwell", "cat-1")));

        selectFirstBook();

        view.getEditButton().doClick();

        verify(controller).updateBook(argThat(book ->
                book.getCategoryId() == null));
    }

    @Test
    void shouldDeleteSelectedBook() {
        Book book = book("book-1", "1984", "George Orwell", "cat-1");

        view.showAllBooks(List.of(book));

        selectFirstBook();

        view.getDeleteButton().doClick();

        verify(controller).deleteBook(book);
    }

    @Test
    void shouldClearFormAfterDeselection() {
        view.showAllBooks(List.of(
                book("book-1", "1984", "George Orwell", "cat-1")));

        selectFirstBook();

        view.getBookList().clearSelection();

        assertThat(view.getTitleField().getText()).isEmpty();
        assertThat(view.getBookBeingEdited()).isNull();
    }

    @Test
    void shouldKeepCategorySelectionWhenCategoryMissing() {
        addCategories(category("cat-1", "Fiction"));

        view.showAllBooks(List.of(
                book("book-1", "1984", "George Orwell", "missing")));

        selectFirstBook();

        assertThat(view.getCategoryCombo().getSelectedIndex())
                .isZero();
    }

    @Test
    void shouldUpdateBookList() {
        Book original = book("1", "1984", "Orwell", "c1");
        Book other = book("2", "Dune", "Herbert", "c2");

        view.showAllBooks(List.of(original, other));

        view.bookUpdated(
                book("1", "Updated", "Orwell", "c1"));

        assertThat(
                view.getBookListModel()
                        .getElementAt(0)
                        .getTitle())
                .isEqualTo("Updated");

        view.bookUpdated(
                book("99", "Ghost", "Unknown", "c1"));

        assertThat(view.getBookListModel().getSize())
                .isEqualTo(2);
    }

    @Test
    void shouldHandleBookAddedAndDeleted() {
        Book book =
                book("1", "1984", "Orwell", "c1");

        view.showError("error");

        view.bookAdded(book);

        assertThat(view.getBookListModel().getSize())
                .isEqualTo(1);

        view.bookDeleted(book);

        assertThat(view.getBookListModel().getSize())
                .isZero();

        assertThat(
                view.getErrorLabel()
                        .getText()
                        .trim())
                .isEmpty();
    }

    @Test
    void shouldReplaceBooksAndCategories() {
        view.showAllBooks(List.of(
                book("1", "A", "B", "C"),
                book("2", "X", "Y", "Z")));

        assertThat(view.getBookListModel().getSize())
                .isEqualTo(2);

        view.showAllBooks(List.of());

        assertThat(view.getBookListModel().getSize())
                .isZero();

        addCategories(
                category("c1", "Fiction"),
                category("c2", "Science"));

        assertThat(view.getCategoryCombo().getItemCount())
                .isEqualTo(2);
    }

    @Test
    void shouldKeepButtonsDisabledForIncompleteForm() {
        view.getTitleField().setText("1984");

        assertThat(view.getAddButton().isEnabled()).isFalse();
        assertThat(view.getEditButton().isEnabled()).isFalse();
    }

    @Test
    void shouldHandleDocumentChangedUpdate() {
        for (DocumentListener listener :
                ((AbstractDocument)
                        view.getTitleField()
                                .getDocument())
                        .getDocumentListeners()) {

            if (listener.getClass()
                    .getEnclosingClass()
                    == BookSwingView.class) {

                listener.changedUpdate(null);
                break;
            }
        }

        assertThat(view.getAddButton().isEnabled())
                .isFalse();
    }

    private void fillForm(String title, String author) {
        view.getTitleField().setText(title);
        view.getAuthorField().setText(author);
    }

    private void selectFirstBook() {
        view.getBookList().setSelectedIndex(0);
    }

    private void addCategories(Category... categories) {
        view.showAllCategories(List.of(categories));
    }

    private Category category(String id, String name) {
        return new Category(id, name);
    }

    private Book book(
            String id,
            String title,
            String author,
            String category) {

        return new Book(
                id,
                title,
                author,
                category);
    }
}