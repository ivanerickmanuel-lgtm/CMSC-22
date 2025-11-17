package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import handler.FileManager;
import model.Student;
import java.nio.file.Path;
import java.util.ArrayList;

public class RegisterView {
    private Stage stage;
    private boolean registrationSuccessful = false;
    private FileManager fileManager;
    private ArrayList<Student> userList;
    private Path savePath;

    public RegisterView(FileManager fileManager, ArrayList<Student> userList, Path savePath) {
        this.fileManager = fileManager;
        this.userList = userList;
        this.savePath = savePath;
        this.stage = new Stage();
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("Register New Account");
        setupUI();
    }

    private void setupUI() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);

        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );

        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Middle Name:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Last Name:"), 0, 3);
        grid.add(lastNameField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Password:"), 0, 5);
        grid.add(passwordField, 1, 5);
        grid.add(new Label("Confirm Password:"), 0, 6);
        grid.add(confirmPasswordField, 1, 6);
        grid.add(new Label("Program:"), 0, 7);
        grid.add(programCombo, 1, 7);

        Label messageLabel = new Label();
        grid.add(messageLabel, 0, 8, 2, 1);

        Button registerButton = new Button("Register");
        Button cancelButton = new Button("Cancel");
        HBox buttonBox = new HBox(10, registerButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 9, 2, 1);

        registerButton.setOnAction(e -> {
            String error = validateFields(firstNameField, lastNameField, emailField, 
                                         passwordField, confirmPasswordField, programCombo);
            if (error != null) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText(error);
            } else {
                Student newStudent = new Student(
                    firstNameField.getText(),
                    middleNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    programCombo.getValue()
                );
                userList.add(newStudent);
                fileManager.save(userList, savePath);
                registrationSuccessful = true;
                stage.close();
            }
        });

        cancelButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(grid, 400, 450);
        stage.setScene(scene);
    }

    private String validateFields(TextField firstName, TextField lastName, TextField email,
                                  PasswordField password, PasswordField confirmPassword,
                                  ComboBox<String> program) {
        if (firstName.getText().isEmpty() || lastName.getText().isEmpty()) {
            return "First and Last name are required";
        }
        if (email.getText().isEmpty() || !email.getText().contains("@")) {
            return "Valid email is required";
        }
        if (password.getText().length() < 4) {
            return "Password must be at least 4 characters";
        }
        if (!password.getText().equals(confirmPassword.getText())) {
            return "Passwords do not match";
        }
        if (program.getValue() == null) {
            return "Please select a program";
        }
        for (Student s : userList) {
            if (s.getEmail().equalsIgnoreCase(email.getText())) {
                return "Email already registered";
            }
        }
        return null;
    }

    public void showAndWait() {
        stage.showAndWait();
    }

    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}