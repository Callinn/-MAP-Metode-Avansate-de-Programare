package com.example.guiex1.controller;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.User;

import com.example.guiex1.service.FriendshipService;
import com.example.guiex1.service.MessageService;
import com.example.guiex1.service.UserService;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class MainController {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    private User loggedInUser;
    ObservableList<Friendship> friendsModel = FXCollections.observableArrayList();

    @FXML
    private TableView<Friendship> tableViewFriendship;
    @FXML
    private TableColumn<Friendship, String> tableColumnFriendName;
    @FXML
    private TableColumn<Friendship, String> tableColumnFriendSince;

    @FXML
    private Button btnAddFriend;
    @FXML
    private Button btnFriendRequests;
    @FXML
    private Button btnDeleteFriend;
    @FXML
    private Button btnMessages;

    @FXML
    private Button btnNextPage;
    @FXML
    private Button btnPreviousPage;
    @FXML
    private Label lblPageInfo;


    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    public void setAll(UserService userService, FriendshipService friendshipService,MessageService messageService,User loggedInUser) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.loggedInUser = loggedInUser;
        this.messageService = messageService;
        initModel();
    }


    @FXML
    public void initialize() {
        // Configure columns
        tableColumnFriendName.setCellValueFactory(data -> {
            Long friendId = data.getValue().getIdUser1().equals(loggedInUser.getId())
                    ? data.getValue().getIdUser2()
                    : data.getValue().getIdUser1();

            User friend = userService.findUserById(friendId).orElse(null); // Găsim utilizatorul
            String friendName = (friend != null) ? friend.getFirstName() + " " + friend.getLastName() : "Unknown";
            return new SimpleStringProperty(friendName);
        });

        tableColumnFriendSince.setCellValueFactory(data -> {
            LocalDate date = data.getValue().getDate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
        });

        // Add event handlers to buttons
        btnAddFriend.setOnAction(event -> handleOpenAddFriendDialog());
        btnFriendRequests.setOnAction(event -> handleOpenFriendRequestsDialog());
        btnDeleteFriend.setOnAction(event -> handleDeleteFriend());
        btnMessages.setOnAction(event -> handleOpenMessagesDialog()); // Set action for messages button

        // Paginare
        btnNextPage.setOnAction(event -> onNextPage());
        btnPreviousPage.setOnAction(event -> onPreviousPage());
    }


    private void initModel() {
        // Obține toate prieteniile
        Page<Friendship> page = friendshipService.findAllOnPage(new Pageable(currentPage, pageSize));

        // Calculăm totalul de prieteni și numărul maxim de pagini
        totalNumberOfElements = page.getTotalNumberOfElements();
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }

        // Dacă pagina curentă este mai mare decât maximul, o resetăm
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = friendshipService.findAllOnPage(new Pageable(currentPage, pageSize));
        }

        // Pagina de prieteni
        List<Friendship> friendshipsPage = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());

        // Actualizează modelul de prieteni
        friendsModel.clear();
        friendsModel.addAll(StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList()));

        tableViewFriendship.setItems(friendsModel);

        // Actualizează butoanele de paginare

        btnPreviousPage.setDisable(currentPage == 0);
        btnNextPage.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);

        // Actualizează eticheta cu informațiile despre pagină
        lblPageInfo.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }


    @FXML
    public void handleOpenFriendRequestsDialog() {
        try {
            if (loggedInUser == null) {
                showErrorMessage("Error", "No user logged in.");
                return;
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/guiex1/views/FriendRequest.fxml"));
            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Requests");
            dialogStage.initOwner(tableViewFriendship.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            FriendRequestsController controller = loader.getController();
            controller.setServices(friendshipService, dialogStage, loggedInUser, userService);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleOpenAddFriendDialog() {
        try {
            if (loggedInUser == null) {
                showErrorMessage("Error", "No user logged in.");
                return;
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/guiex1/views/AddFriendView.fxml"));
            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initOwner(tableViewFriendship.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            AddFriendController controller = loader.getController();
            controller.setServices(friendshipService, loggedInUser, dialogStage, userService);

            dialogStage.showAndWait();
            initModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteFriend() {
        Friendship selectedFriendship = tableViewFriendship.getSelectionModel().getSelectedItem();
        if (selectedFriendship != null) {
            try {
                friendshipService.removeFriendship(selectedFriendship.getIdUser1(), selectedFriendship.getIdUser2());
                showConfirmationMessage("Friendship Deleted", "The friendship has been deleted.");
                initModel();
            } catch (Exception e) {
                showErrorMessage("Error", "Failed to delete friendship: " + e.getMessage());
            }
        } else {
            showErrorMessage("Error", "No friendship selected.");
        }
    }

    @FXML
    public void handleOpenMessagesDialog() {
        try {
            if (loggedInUser == null) {
                showErrorMessage("Error", "No user logged in.");
                return;
            }
            Friendship selectedFriendship = tableViewFriendship.getSelectionModel().getSelectedItem();
            if (selectedFriendship == null) {
                showErrorMessage("Error", "No friend selected.");
                return;
            }

            Long friendId = selectedFriendship.getIdUser1().equals(loggedInUser.getId()) ? selectedFriendship.getIdUser2() : selectedFriendship.getIdUser1();
            User friend = userService.findUserById(friendId).orElse(null); // Get the friend details

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/guiex1/views/ChatView.fxml"));
            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Messages");
            dialogStage.initOwner(tableViewFriendship.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            ChatController controller = loader.getController();
            controller.setServices(messageService,loggedInUser,friend);

            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showConfirmationMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onNextPage() {
        currentPage ++;
        initModel();
    }

    @FXML
    public void onPreviousPage() {
        currentPage --;
        initModel();
    }


    private void showErrorMessage(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
