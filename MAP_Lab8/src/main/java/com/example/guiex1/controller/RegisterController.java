package com.example.guiex1.controller;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.User;
import com.example.guiex1.domain.UserCredentials;
import com.example.guiex1.domain.validators.ValidationException;
import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.MessageService;
import com.example.guiex1.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class RegisterController {
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService= friendshipService ;
    }
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @FXML
    private void handleRegister() {
        try {
            // Obține datele introduse
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            // Verifică dacă toate câmpurile sunt completate
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled!");
                return;
            }

            // Verifică dacă utilizatorul există deja cu acest email
            Optional<User> existingUser = userService.findUserByEmailAndPassword(email,password);
            if (existingUser.isPresent()) {
                showAlert(Alert.AlertType.ERROR, "Error", "A user with this email already exists!");
                return;
            }

            // Creează un nou utilizator
            User newUser = new User(firstName, lastName);
            userService.addUser(newUser);

            // Verifică dacă utilizatorul a fost creat cu succes
            if (newUser != null) {
                // Create and save credentials
                UserCredentials userCredentials = new UserCredentials(Math.toIntExact(newUser.getId()), email, password);
                userService.addCredentials(userCredentials);

                // Retrieve the current user as Optional<User>
                Optional<User> currentUser = Optional.of(newUser);

                // Show success message and load the main page
                showAlert(Alert.AlertType.INFORMATION, "Success", "User registered successfully!");
                loadMainPage(currentUser);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "User could not be created!");
            }
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMainPage(Optional<User> currentUser) {
        if (currentUser.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "User could not be created!");
            return;
        }

        try {
            // Load main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/MainView.fxml"));
            Parent root = loader.load();

            // Set controller parameters
            MainController mainController = loader.getController();
            mainController.setAll(userService, friendshipService, messageService, currentUser.get());

            // Change scene
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load main page", e);
        }
    }
}
