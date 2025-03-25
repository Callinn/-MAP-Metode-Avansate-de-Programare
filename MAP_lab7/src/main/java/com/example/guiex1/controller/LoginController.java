package com.example.guiex1.controller;

import com.example.guiex1.domain.User;
import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.MessageService;
import com.example.guiex1.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private FriendshipService friendshipService;
    private UserService userService;
    private MessageService messageService;

    public void setService(UserService userService1, FriendshipService friendshipService1, MessageService messageService1) {
        this.userService = userService1;
        this.friendshipService = friendshipService1;
        this.messageService = messageService1;
    }

    public void setInitMsg(String initMsg) {
        addedText.setText(initMsg);
    }

    @FXML
    private TextField emailText;
    @FXML
    private TextField passwordText;
    @FXML
    private Label addedText;

    @FXML
    public void onLoginButtonClick() throws IOException {
        String email = emailText.getText().trim();
        String password = passwordText.getText().trim();
        Optional<User> user = userService.findUserByEmailAndPassword(email, password);
        if (user.isEmpty()) {
            addedText.setText("Invalid credentials! Please try again!");
            passwordText.setText("");
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/MainView.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();

            // Pass the actual User object to the main controller
            mainController.setAll(userService, friendshipService, messageService, user.get());

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

            closeWindow();
        }
    }

    public void registerAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/guiex1/views/RegisterWindow.fxml"));
        Pane root = fxmlLoader.load();
        RegisterController registerWindow = fxmlLoader.getController();
        registerWindow.setUserService(userService);
        registerWindow.setFriendshipService(friendshipService);
        registerWindow.setMessageService(messageService);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

        closeWindow();
    }

    private void closeWindow() {
        Stage thisStage = (Stage) addedText.getScene().getWindow();
        thisStage.close();
    }
}
