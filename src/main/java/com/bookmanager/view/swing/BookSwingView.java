package com.bookmanager.view.swing;

import com.bookmanager.controller.BookController;
import com.bookmanager.model.Book;
import com.bookmanager.model.Category;
import com.bookmanager.view.BookView;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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

public class BookSwingView extends JFrame implements BookView {

    private static final long serialVersionUID = 1L;

    private BookController controller;

    JTextField titleField;
    JTextField authorField;
    JComboBox<Category> categoryCombo;
    JButton addButton;
    JButton deleteButton;
    DefaultListModel<Book> listModel;
    JList<Book> bookList;
    JLabel errorLabel;

    public BookSwingView() {
        setTitle("Book Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
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

        DocumentListener enabler = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAddButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAddButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAddButton();
            }
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
        panel.add(new JLabel(""));
        panel.add(addButton);
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
                deleteButton.setEnabled(bookList.getSelectedIndex() != -1);
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

    private void onDeleteBook() {
        Book selected = bookList.getSelectedValue();
        if (selected != null) {
            controller.deleteBook(selected);
        }
    }

    private void updateAddButton() {
        boolean ready = !titleField.getText().trim().isEmpty()
                && !authorField.getText().trim().isEmpty();
        addButton.setEnabled(ready);
    }

    public void setController(BookController controller) {
        this.controller = controller;
    }

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
        titleField.setText("");
        authorField.setText("");
        errorLabel.setText(" ");
    }

    @Override
    public void bookDeleted(Book book) {
        listModel.removeElement(book);
        errorLabel.setText(" ");
    }

    @Override
    public void showError(String message) {
        errorLabel.setText(message);
    }


    public JButton getAddButton() {
        return addButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JTextField getTitleField() {
        return titleField;
    }

    public JTextField getAuthorField() {
        return authorField;
    }

    public JList<Book> getBookList() {
        return bookList;
    }

    public DefaultListModel<Book> getBookListModel() {
        return listModel;
    }

    public JComboBox<Category> getCategoryCombo() {
        return categoryCombo;
    }

    public JLabel getErrorLabel() {
        return errorLabel;
    }
}