package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import handler.FileManager;
import model.Student;

import java.nio.file.Path;
import java.util.ArrayList;

public class LoginView {
    private Stage stage;
    private Scene scene;
    private VBox mainContainer;
    private GridPane grid;
    
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    
    private FileManager fileManager;
    private ArrayList<Student> userList;
    private Path savePath;

    public LoginView() {
        this.fileManager = new FileManager();
        this.savePath = FileManager.getSavePath();
        this.userList = fileManager.load(savePath);
        
        if (userList == null) {
            userList = new ArrayList<>();
        }
        
        setProperties();
    }

    private void setProperties() {
        // Main container to hold logo and grid
        mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(25));
        
        // Load and display ICS logo
        try {
            Image logo = new Image("file:src/img/ics-logo.png");
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(150);
            logoView.setPreserveRatio(true);
            mainContainer.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }
        
        // Initialize grid layout
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        
        // Title
        Label titleLabel = new Label("ICS Registration Planner");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);
        
        // Initialize text fields
        emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setPrefWidth(250);
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        
        // Initialize labels
        Label emailLabel = new Label("Email:");
        Label passwordLabel = new Label("Password:");
        
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);

        // Message label for errors
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        grid.add(messageLabel, 0, 3, 2, 1);

        // Initialize buttons
        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        // Setup button layout
        HBox buttonBox = new HBox(10, loginBtn, registerBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 4, 2, 1);

        // Button event handlers
        loginBtn.setOnAction(e -> handleLogin());
        registerBtn.setOnAction(e -> handleRegister());

        // Add grid to main container
        mainContainer.getChildren().add(grid);
        
        // Create scene
        scene = new Scene(mainContainer, 400, 400);
    }

    private void handleLogin() {
        // Validate fields are not empty
        if (emailField.getText().trim().isEmpty() || 
            passwordField.getText().trim().isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Check credentials
        Student foundStudent = null;
        for (Student student : userList) {
            if (student.getEmail().equalsIgnoreCase(email) && 
                student.getPassword().equals(password)) {
                foundStudent = student;
                break;
            }
        }
        
        if (foundStudent != null) {
            System.out.println("Login successful for: " + foundStudent.getFullName());
            StudentDashboard dashboard = new StudentDashboard(foundStudent);
            dashboard.setStage(stage);
        } else {
            messageLabel.setText("Invalid email or password");
        }
    }

    private void handleRegister() {
        RegisterView registerView = new RegisterView(fileManager, userList, savePath);
        registerView.showAndWait();
        
        if (registerView.isRegistrationSuccessful()) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Registration successful! Please login.");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ICS Registration Planner - Login");
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}