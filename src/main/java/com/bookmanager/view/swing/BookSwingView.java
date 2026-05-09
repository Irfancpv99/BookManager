package com.bookmanager.view.swing;

import com.bookmanager.controller.BookController;
import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.view.BookView;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.UUID;

public class BookSwingView extends JPanel implements BookView {

    private static final long serialVersionUID = 1L;

    private BookController controller;

    // track which book is being edited (null = add mode)
    private Book bookBeingEdited;

    JTextField titleField;
    JTextField authorField;
    JComboBox<Category> categoryCombo;
    JButton addButton;
    JButton editButton;
    JButton deleteButton;
    DefaultListModel<Book> listModel;
    JList<Book> bookList;
    JLabel errorLabel;

    public BookSwingView() {
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildListPanel(), BorderLayout.CENTER);
        add(buildErrorLabel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        titleField = new JTextField();
        titleField.setName("titleField");

        authorField = new JTextField();
        authorField.setName("authorField");

        categoryCombo = new JComboBox<>();
        categoryCombo.setName("categoryCombo");

        addButton = new JButton("Add Book");
        addButton.setName("addButton");
        addButton.setEnabled(false);
        addButton.addActionListener(e -> onAddBook());

        editButton = new JButton("Save Edit");
        editButton.setName("editButton");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> onEditBook());

        DocumentListener enabler = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { updateButtons(); }
            @Override public void removeUpdate(DocumentEvent e)  { updateButtons(); }
            @Override public void changedUpdate(DocumentEvent e) { updateButtons(); }
        };

        titleField.getDocument().addDocumentListener(enabler);
        authorField.getDocument().addDocumentListener(enabler);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);
        panel.add(addButton);
        panel.add(editButton);
        return panel;
    }

    private JPanel buildListPanel() {
        listModel = new DefaultListModel<>();

        bookList = new JList<>(listModel);
        bookList.setName("bookList");
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        deleteButton = new JButton("Delete Selected");
        deleteButton.setName("deleteButton");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> onDeleteBook());

        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onBookSelected();
            }
        });

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 4, 8));
        panel.add(new JScrollPane(bookList), BorderLayout.CENTER);
        panel.add(deleteButton, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel buildErrorLabel() {
        errorLabel = new JLabel(" ");
        errorLabel.setName("errorLabel");
        errorLabel.setForeground(Color.RED);
        return errorLabel;
    }

    // -------------------------------------------------------------------------
    // UI event handlers
    // -------------------------------------------------------------------------

    private void onBookSelected() {
        Book selected = bookList.getSelectedValue();
        if (selected == null) {
            clearForm();
            bookBeingEdited = null;
            deleteButton.setEnabled(false);
        } else {
            // populate form fields with the selected book
            bookBeingEdited = selected;
            titleField.setText(selected.getTitle());
            authorField.setText(selected.getAuthor());
            selectCategory(selected.getCategoryId());
            deleteButton.setEnabled(true);
        }
        updateButtons();
    }

    private void onAddBook() {
        Category selected = (Category) categoryCombo.getSelectedItem();
        String categoryId = selected != null ? selected.getId() : null;
        Book book = new Book(
                UUID.randomUUID().toString(),
                titleField.getText().trim(),
                authorField.getText().trim(),
                categoryId);
        controller.addBook(book);
    }

    private void onEditBook() {
        if (bookBeingEdited == null) return;
        Category selected = (Category) categoryCombo.getSelectedItem();
        String categoryId = selected != null ? selected.getId() : null;
        Book updated = new Book(
                bookBeingEdited.getId(),
                titleField.getText().trim(),
                authorField.getText().trim(),
                categoryId);
        controller.updateBook(updated);
    }

    private void onDeleteBook() {
        Book selected = bookList.getSelectedValue();
        if (selected != null) {
            controller.deleteBook(selected);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void selectCategory(String categoryId) {
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getId().equals(categoryId)) {
                categoryCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void clearForm() {
        titleField.setText("");
        authorField.setText("");
        errorLabel.setText(" ");
    }

    private void updateButtons() {
        boolean fieldsReady = !titleField.getText().trim().isEmpty()
                && !authorField.getText().trim().isEmpty();
        // Add mode: no book selected
        addButton.setEnabled(fieldsReady && bookBeingEdited == null);
        // Edit mode: a book is selected
        editButton.setEnabled(fieldsReady && bookBeingEdited != null);
    }

    // -------------------------------------------------------------------------
    // BookView callbacks
    // -------------------------------------------------------------------------

    @Override
    public void showAllBooks(List<Book> books) {
        listModel.clear();
        books.forEach(listModel::addElement);
    }

    @Override
    public void showAllCategories(List<Category> categories) {
        categoryCombo.removeAllItems();
        categories.forEach(categoryCombo::addItem);
    }

    @Override
    public void bookAdded(Book book) {
        listModel.addElement(book);
        clearForm();
        bookBeingEdited = null;
        updateButtons();
    }

    @Override
    public void bookUpdated(Book book) {
        // replace the old entry in the list model
        for (int i = 0; i < listModel.getSize(); i++) {
            if (listModel.getElementAt(i).getId().equals(book.getId())) {
                listModel.set(i, book);
                break;
            }
        }
        clearForm();
        bookList.clearSelection();
        bookBeingEdited = null;
        updateButtons();
    }

    @Override
    public void bookDeleted(Book book) {
        listModel.removeElement(book);
        clearForm();
        bookBeingEdited = null;
        updateButtons();
        errorLabel.setText(" ");
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message);
    }

    // -------------------------------------------------------------------------
    // Accessors (for tests)
    // -------------------------------------------------------------------------

    public void setController(BookController controller) { this.controller = controller; }
    public JButton getAddButton()                        { return addButton; }
    public JButton getEditButton()                       { return editButton; }
    public JButton getDeleteButton()                     { return deleteButton; }
    public JTextField getTitleField()                    { return titleField; }
    public JTextField getAuthorField()                   { return authorField; }
    public JList<Book> getBookList()                     { return bookList; }
    public DefaultListModel<Book> getBookListModel()     { return listModel; }
    public JComboBox<Category> getCategoryCombo()        { return categoryCombo; }
    public JLabel getErrorLabel()                        { return errorLabel; }
    public Book getBookBeingEdited()                     { return bookBeingEdited; }
}