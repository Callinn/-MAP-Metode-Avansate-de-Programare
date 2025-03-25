package com.example.map_lab6.src.controller;



import com.example.map_lab6.src.domain.Friendship;
import com.example.map_lab6.src.domain.User;
import com.example.map_lab6.src.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainController {
    @FXML
     TableView<User> userTable;
    @FXML
     TableColumn<User, String> firstNameColumn;
    @FXML
    TableColumn<User, String> lastNameColumn;

    @FXML
    Button deleteFriendButton;
    @FXML
    Button addFriendButton;
    @FXML
    Button viewRequestsButton;

    @FXML
    ListView<User> friendListView;
    @FXML
     VBox friendActions;

    @FXML
    Button addUserButton;
    @FXML
    Button deleteUserButton;

    SocialNetwork socialNetwork;
    ObservableList<User> userModel = FXCollections.observableArrayList();
    ObservableList<User> friendModel = FXCollections.observableArrayList();

    public void setService(SocialNetwork socialNetwork) {
        this.socialNetwork = socialNetwork;
        loadUsers();
    }

    @FXML
    public void initialize() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showUserFriends(newValue);
            }
        });

        userTable.setItems(userModel);
        friendListView.setItems(friendModel);
    }

    private void loadUsers() {
        List<User> users = (List<User>) socialNetwork.getAll();
        userModel.setAll(users);
    }

    private void showUserFriends(User selectedUser) {
        Iterable<Friendship> friends = socialNetwork.getFriendships();
        friendModel.setAll((User) friends);

        friendActions.setVisible(true);
    }

    @FXML
    public void handleAddUser(ActionEvent event) {
        // Deschide un dialog pentru adăugarea unui utilizator
        User newUser = new User("Nume", "Prenume");
        User addedUser = socialNetwork.addUser(newUser);
        if (addedUser != null) {
            userModel.add(addedUser);
        }
    }

    @FXML
    public void handleDeleteUser(ActionEvent event) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            socialNetwork.removeUser(selectedUser.getId());
            userModel.remove(selectedUser);
            friendModel.clear();
        } else {
            showAlert("Eroare", "Nu a fost selectat niciun utilizator.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
