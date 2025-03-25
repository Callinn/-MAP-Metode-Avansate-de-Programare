package com.example.map_lab6.src;

import com.example.map_lab6.src.controller.MainController;
import com.example.map_lab6.src.domain.User;
import com.example.map_lab6.src.domain.validators.FriendshipValidator;
import com.example.map_lab6.src.domain.validators.UserValidator;
import com.example.map_lab6.src.repository.Repository;
import com.example.map_lab6.src.repository.database.FriendshipDBRepository;
import com.example.map_lab6.src.repository.database.UserDBRepository;
import com.example.map_lab6.src.service.SocialNetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApp extends Application {
     SocialNetwork socialNetwork;
    public static void Main(String[] args) {launch(); }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Reading data from database");
        Repository<Long, User> userRepository = new UserDBRepository(new UserValidator());

        socialNetwork = new SocialNetwork(new UserDBRepository(new UserValidator()), new FriendshipDBRepository(new FriendshipValidator(new UserDBRepository(new UserValidator()))));
        initView(stage);
        stage.setWidth(800);
        stage.show();
    }

    private void initView(Stage stage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/example/map_lab6/main-view.fxml"));
        AnchorPane userLayout = fxmlLoader.load();
        stage.setScene(new Scene(userLayout));

        MainController mainController = fxmlLoader.getController();
        mainController.setService(socialNetwork);

    }

}
