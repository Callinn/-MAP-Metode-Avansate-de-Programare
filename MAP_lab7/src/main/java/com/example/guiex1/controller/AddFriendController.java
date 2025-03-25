package com.example.guiex1.controller;

import com.example.guiex1.domain.User;
import com.example.guiex1.domain.Friendship;
import com.example.guiex1.repository.database.UserDBRepository;
import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendController {
    private UserDBRepository userDBRepository;
    private FriendshipService friendshipService;
    private UserService userService;
    private User selectedUser;
    private Stage dialogStage;

    @FXML
    private TextField textFieldSearch;

    @FXML
    private TableView<User> tableViewUsers;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TableColumn<User, String> tableColumnLastName;

    @FXML
    private Button buttonSendRequest;

    private ObservableList<User> potentialFriendsList = FXCollections.observableArrayList();

    public void setServices(FriendshipService friendshipService, User selectedUser, Stage dialogStage, UserService userService) {
        this.friendshipService = friendshipService;
        this.selectedUser = selectedUser;
        this.dialogStage = dialogStage;
        this.userService = userService;

        loadPotentialFriends("");
    }

    @FXML
    public void initialize() {
        // Configure columns
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User,String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User,String>("lastName"));

        tableViewUsers.setItems(potentialFriendsList);

        // Add listener to search bar
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> loadPotentialFriends(newValue));

        // Initially disable the button until a user is selected
        buttonSendRequest.setDisable(true);

        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            buttonSendRequest.setDisable(newValue == null);
        });
    }

    private void loadPotentialFriends(String filter) {
        List<User> filteredUsers = new ArrayList<>();

        // Get all users
        for (User user : userService.getAll()) {
            if (!user.equals(selectedUser)) { // Exclude the selected user
                boolean isFriend = friendshipService.areFriends(selectedUser.getId(), user.getId());
                boolean hasPendingRequest = friendshipService.hasPendingRequest(selectedUser.getId(), user.getId())||friendshipService.hasPendingRequest(user.getId(),selectedUser.getId());

                if (!isFriend && !hasPendingRequest && (
                        user.getFirstName().toLowerCase().contains(filter.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(filter.toLowerCase()))) {
                    filteredUsers.add(user);
                }
            }
        }

        potentialFriendsList.setAll(filteredUsers);
    }


    @FXML
    private void handleSendFriendRequest() {
        User selectedPotentialFriend = tableViewUsers.getSelectionModel().getSelectedItem();

        if (selectedPotentialFriend == null) {
            showErrorMessage("Error", "Please select a user to send a friend request.");
            return;
        }

        try {
            friendshipService.sendFriendRequest(selectedUser.getId(), selectedPotentialFriend.getId());
            showConfirmationMessage("Success", "Friend request sent successfully!");

            // Reload the table to exclude the user who just received the request
            loadPotentialFriends(textFieldSearch.getText());
        } catch (IllegalStateException | IllegalArgumentException e) {
            showErrorMessage("Error", "Failed to send friend request: " + e.getLocalizedMessage());
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }


    private void showErrorMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
