import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;

public class Main2 extends Application {
    static {
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }
    
    private ObservableList<Book> books = FXCollections.observableArrayList(
        new Book("Advanced Java Programming", "Herbert Schildt", "978-1260440232", "Technology", 10),
        new Book("Database Management Systems", "Raghu Ramakrishnan", "978-0072465631", "Technology", 8),
        new Book("Computer Networks", "Andrew S. Tanenbaum", "978-0132126953", "Technology", 6),
        new Book("Operating System Concepts", "Abraham Silberschatz", "978-1118063330", "Technology", 5),
        new Book("Data Structures and Algorithms", "Robert Lafore", "978-0672324536", "Technology", 7)
    );
    
    private ListView<Book> bookList = new ListView<>();
    private ObservableList<String> categories = FXCollections.observableArrayList(
        "Technology", "Science", "Mathematics", "Literature", "History"
    );
    
    private TabPane tabPane;
    private ComboBox<String> categoryBox;
    
    private class Book {
        String title, author, isbn, category;
        int totalCopies;
        int availableCopies;
        ObservableList<BookCopy> copies = FXCollections.observableArrayList();
        
        public Book(String title, String author, String isbn, String category, int totalCopies) {
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.category = category;
            this.totalCopies = totalCopies;
            this.availableCopies = totalCopies;
            
            for (int i = 1; i <= totalCopies; i++) {
                copies.add(new BookCopy(i));
            }
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%02d. ", books.indexOf(this) + 1));
            sb.append(String.format("%-40s", title));
            sb.append(" | Author: ").append(String.format("%-25s", author));
            sb.append(" | Category: ").append(String.format("%-10s", category));
            sb.append(" | Available: ").append(availableCopies).append("/").append(totalCopies);
            return sb.toString();
        }
    }
    
    private class BookCopy {
        int copyNumber;
        boolean isCheckedOut;
        String borrower;
        LocalDate dueDate;
        boolean isForReading;
        
        public BookCopy(int copyNumber) {
            this.copyNumber = copyNumber;
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        tabPane = new TabPane();
        
        // Admin Section
        Tab adminTab = new Tab("Admin Section");
        adminTab.setClosable(false);
        VBox adminBox = createAdminSection();
        adminTab.setContent(adminBox);
        
        // Student/Faculty Section
        Tab userTab = new Tab("Student/Faculty Section");
        userTab.setClosable(false);
        VBox userBox = createUserSection();
        userTab.setContent(userBox);
        
        tabPane.getTabs().addAll(adminTab, userTab);
        
        Scene scene = new Scene(tabPane, 1200, 800);
        primaryStage.setTitle("College Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createAdminSection() {
        VBox adminBox = new VBox(10);
        adminBox.setPadding(new Insets(10));
        
        // Book Details Table
        TableView<Book> bookTable = new TableView<>();
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().title));
        titleCol.setPrefWidth(200);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().author));
        authorCol.setPrefWidth(150);
        
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isbn));
        isbnCol.setPrefWidth(120);
        
        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().category));
        categoryCol.setPrefWidth(100);
        
        TableColumn<Book, String> copiesCol = new TableColumn<>("Copies");
        copiesCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().availableCopies + "/" + data.getValue().totalCopies));
        copiesCol.setPrefWidth(80);
        
        bookTable.getColumns().addAll(titleCol, authorCol, isbnCol, categoryCol, copiesCol);
        bookTable.setItems(books);
        bookTable.setPrefHeight(300);
        
        // New Book Form
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(20, 0, 0, 0));
        
        Label addBookLabel = new Label("Add New Book");
        addBookLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Book Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author Name");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        categoryBox = new ComboBox<>(categories);
        categoryBox.setPromptText("Select Category");
        TextField copiesField = new TextField();
        copiesField.setPromptText("Number of Copies");
        
        HBox formControls = new HBox(10);
        Button addButton = new Button("Add New Book");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            try {
                int copies = Integer.parseInt(copiesField.getText());
                Book book = new Book(
                    titleField.getText(),
                    authorField.getText(),
                    isbnField.getText(),
                    categoryBox.getValue(),
                    copies
                );
                books.add(book);
                clearFields(titleField, authorField, isbnField, copiesField);
                categoryBox.setValue(null);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number of copies");
            }
        });
        
        Label titleLabel = new Label("Library Administration");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        adminBox.getChildren().addAll(
            titleLabel,
            new Separator(),
            new Label("Add New Book:"),
            titleField,
            authorField,
            isbnField,
            categoryBox,
            copiesField,
            formControls
        );
        
        formControls.getChildren().addAll(addButton);
        formBox.getChildren().addAll(addBookLabel, titleField, authorField, isbnField, categoryBox, copiesField, formControls);
        
        adminBox.getChildren().addAll(
            bookTable,
            formBox
        );
        
        return adminBox;
    }
    
    private VBox createUserSection() {
        VBox userBox = new VBox(10);
        userBox.setPadding(new Insets(10));
        
        TextField borrowerField = new TextField();
        borrowerField.setPromptText("Enter Your Name");
        
        HBox buttonBox = new HBox(10);
        Button issueButton = new Button("Issue Book");
        Button returnButton = new Button("Return Book");
        Button readingButton = new Button("Take for Reading");
        buttonBox.getChildren().addAll(issueButton, returnButton, readingButton);
        
        issueButton.setOnAction(e -> issueBook(borrowerField.getText(), false));
        returnButton.setOnAction(e -> returnBook());
        readingButton.setOnAction(e -> issueBook(borrowerField.getText(), true));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.textProperty().addListener((obs, old, newValue) -> filterBooks(newValue));
        
        bookList.setPrefHeight(500);
        
        Label titleLabel = new Label("Student/Faculty Section");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        userBox.getChildren().addAll(
            titleLabel,
            new Separator(),
            searchField,
            borrowerField,
            buttonBox,
            bookList
        );
        
        bookList.setItems(books);
        return userBox;
    }
    
    private void issueBook(String borrower, boolean forReading) {
        if (borrower.isEmpty()) {
            showAlert("Error", "Please enter your name");
            return;
        }
        
        Book selectedBook = bookList.getSelectionModel().getSelectedItem();
        if (selectedBook != null && selectedBook.availableCopies > 0) {
            for (BookCopy copy : selectedBook.copies) {
                if (!copy.isCheckedOut) {
                    copy.isCheckedOut = true;
                    copy.borrower = borrower;
                    copy.isForReading = forReading;
                    copy.dueDate = forReading ? LocalDate.now() : LocalDate.now().plusDays(14);
                    selectedBook.availableCopies--;
                    bookList.refresh();
                    showAlert("Success", forReading ? 
                        "Book taken for reading room use" : 
                        "Book issued successfully. Due date: " + copy.dueDate);
                    break;
                }
            }
        } else if (selectedBook != null) {
            showAlert("Error", "No copies available");
        }
    }
    
    private void returnBook() {
        Book selectedBook = bookList.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            boolean returned = false;
            for (BookCopy copy : selectedBook.copies) {
                if (copy.isCheckedOut) {
                    copy.isCheckedOut = false;
                    copy.borrower = null;
                    copy.dueDate = null;
                    copy.isForReading = false;
                    selectedBook.availableCopies++;
                    returned = true;
                    break;
                }
            }
            if (returned) {
                bookList.refresh();
                showAlert("Success", "Book returned successfully");
            } else {
                showAlert("Error", "No copies to return");
            }
        }
    }
    
    private void filterBooks(String searchText) {
        ObservableList<Book> filteredList = FXCollections.observableArrayList();
        for (Book book : books) {
            if (book.title.toLowerCase().contains(searchText.toLowerCase()) ||
                book.author.toLowerCase().contains(searchText.toLowerCase()) ||
                book.isbn.contains(searchText)) {
                filteredList.add(book);
            }
        }
        bookList.setItems(filteredList);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}