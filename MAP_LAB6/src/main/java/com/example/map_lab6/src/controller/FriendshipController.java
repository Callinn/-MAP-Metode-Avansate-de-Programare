package com.example.map_lab6.src.controller;


import com.example.map_lab6.src.domain.Friendship;
import com.example.map_lab6.src.domain.User;
import com.example.map_lab6.src.repository.database.FriendshipDBRepository;
import com.example.map_lab6.src.repository.database.UserDBRepository;
import com.example.map_lab6.src.service.SocialNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendshipController {
    @FXML
    private ListView<User> friendRequestsList;

    private SocialNetwork socialNetwork;
    private Long currentUserId;
    private ObservableList<User> friendRequestsModel = FXCollections.observableArrayList();

    public void setService(SocialNetwork utilizatorService, Long userId) {
        this.socialNetwork = utilizatorService;
        this.currentUserId = userId;
        loadFriendRequests();
    }


    private void loadFriendRequests() {
        // Apelăm metoda loadFriendRequests din SocialNetwork pentru a obține lista de cereri de prietenie
        var friendRequests = socialNetwork.loadFriendRequests(currentUserId);

        // Adăugăm utilizatorii care au trimis cereri de prietenie în modelul ListView
        friendRequestsModel.setAll(friendRequests);

        // Setăm modelul pentru ListView
        friendRequestsList.setItems(friendRequestsModel);
    }


    @FXML
    public void handleAcceptRequest() {
        User selectedRequest = friendRequestsList.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            socialNetwork.acceptFriendRequest(currentUserId, selectedRequest.getId());
            friendRequestsModel.remove(selectedRequest);
        } else {
            showAlert("Eroare", "Nu a fost selectată nicio cerere.");
        }
    }

    @FXML
    public void handleRejectRequest() {
        User selectedRequest = friendRequestsList.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            socialNetwork.rejectFriendRequest(currentUserId, selectedRequest.getId());
            friendRequestsModel.remove(selectedRequest);
        } else {
            showAlert("Eroare", "Nu a fost selectată nicio cerere.");
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
