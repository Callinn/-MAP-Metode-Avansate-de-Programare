package com.example.guiex1;

import com.example.guiex1.controller.LoginController;
import com.example.guiex1.domain.validators.*;
import com.example.guiex1.repository.database.*;
import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.MessageService;
import com.example.guiex1.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    public static void main(String[] args){launch(args);}

    @Override
    public void start(Stage primaryStage) throws IOException{
        System.out.println("Reading data from databse");
        String username="postgres";
        String password="admin";
        String url="jdbc:postgresql://localhost:5432/network";
        UserDBRepository userRepository = new UserDBRepository(url,username,password,new UserValidator());
        FriendshipValidator friendshipValidator = new FriendshipValidator(userRepository);
        FriendRequestValidator friendRequestValidator= new FriendRequestValidator();
        MessageValidator messageValidator = new MessageValidator();
        CredentialsValidator credentialsValidator = new CredentialsValidator();
        FriendshipDBRepository friendshipRepository = new FriendshipDBRepository(url, username, password,friendshipValidator);
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository(url,username,password,friendRequestValidator);
        UserCredentialsDBRepository credentialsRepository = new UserCredentialsDBRepository(url, username, password,credentialsValidator); // Initialize credentials repository
        MessageDBRepository messageDBRepository = new MessageDBRepository(url,username,password,messageValidator);
        userService = new UserService(userRepository,friendshipRepository,friendRequestDBRepository,credentialsRepository);
        friendshipService = new FriendshipService(friendshipRepository,friendRequestDBRepository,userRepository);
        messageService = new MessageService(messageDBRepository);
    initLoginView(primaryStage);
    primaryStage.setWidth(300);
    primaryStage.show();
    }

    private void initLoginView(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/Login.fxml"));

        VBox loginLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setService(userService, friendshipService,messageService);
    }

}
