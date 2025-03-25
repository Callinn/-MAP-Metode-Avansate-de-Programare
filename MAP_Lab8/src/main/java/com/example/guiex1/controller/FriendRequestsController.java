package com.example.guiex1.controller;

import com.example.guiex1.domain.User;
import com.example.guiex1.domain.FriendRequest;
import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FriendRequestsController {
    private UserService userService;
    private FriendshipService friendshipService;
    private Stage dialogStage;
    private User selectedUser;

    private ObservableList<FriendRequest> friendRequestsModel = FXCollections.observableArrayList();

    @FXML
    private TableView<FriendRequest> friendRequestsTableView;
    @FXML
    private TableColumn<FriendRequest, String> requesterNameColumn;
    @FXML
    private TableColumn<FriendRequest, String> requestDateColumn;


    public void setServices(FriendshipService friendshipService, Stage dialogStage, User selectedUser,UserService userService) {
        this.friendshipService = friendshipService;
        this.dialogStage = dialogStage;
        this.selectedUser = selectedUser;
        this.userService = userService;
        loadFriendRequests();
    }

    @FXML
    public void initialize() {
        // Setăm celulele tabelului
        requesterNameColumn.setCellValueFactory(data -> {
            Long senderId = data.getValue().getSenderId();
            String name = getSenderFullName(senderId);
            return new SimpleStringProperty(name);
        });

        requestDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateSent().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
    }

    private String getSenderFullName(Long senderId) {
        try {
            Optional<User> sender = userService.findUserById(senderId);
            if (sender.isPresent()) {
                User user = sender.get();
                return user.getFirstName() + " " + user.getLastName();
            } else {
                return "Unknown User";
            }
        } catch (Exception e) {
            // Log error or show appropriate message
            System.err.println("Failed to find user with ID " + senderId + ": " + e.getMessage());
            return "Error fetching user";
        }
    }


    private void loadFriendRequests() {
        friendRequestsModel.clear();
        friendRequestsModel.addAll(friendshipService.getFriendRequests(selectedUser.getId()));
        friendRequestsTableView.setItems(friendRequestsModel);
    }

    @FXML
    private void handleAcceptRequest() {
        FriendRequest selectedRequest = friendRequestsTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            try {
                friendshipService.acceptFriendRequest(selectedRequest);  // Adaugă prietenia
                friendshipService.denyFriendRequest(selectedRequest.getId()); // Șterge cererea din baza de date
                loadFriendRequests();
                showConfirmationMessage("Request Accepted", "The friend request has been accepted.");
            } catch (Exception e) {
                showErrorMessage("Error", "Failed to accept friend request: " + e.getMessage());
            }
        } else {
            showErrorMessage("Error", "No request selected.");
        }
    }


    @FXML
    private void handleDenyRequest() {
        FriendRequest selectedRequest = friendRequestsTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            try {
                friendshipService.denyFriendRequest(selectedRequest.getId());
                friendRequestsModel.remove(selectedRequest);
                showConfirmationMessage("Request Denied", "The friend request has been denied.");
            } catch (Exception e) {
                showErrorMessage("Error", "Failed to deny friend request: " + e.getMessage());
            }
        } else {
            showErrorMessage("Error", "No request selected.");
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
