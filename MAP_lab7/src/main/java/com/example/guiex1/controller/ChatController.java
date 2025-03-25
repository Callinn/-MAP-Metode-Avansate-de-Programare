package com.example.guiex1.controller;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.User;
import com.example.guiex1.service.MessageService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatController {
    private MessageService messageService;
    private User loggedInUser;
    private User friend;

    private ScheduledExecutorService scheduler; // Executor pentru polling

    ObservableList<Message> messageModel = FXCollections.observableArrayList();

    @FXML
    private ListView<Message> listViewMessages;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private Button buttonSend;

    public void setServices(MessageService messageService, User loggedInUser, User friend) {
        this.messageService = messageService;
        this.loggedInUser = loggedInUser;
        this.friend = friend;
        initModel();
        startMessagePolling(); // Inițializezi polling-ul periodic
    }

    @FXML
    public void initialize() {
        // Configure ListView to display messages
        listViewMessages.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    String sender = message.getFrom().equals(loggedInUser.getId()) ? "You" : friend.getFirstName() + " " + friend.getLastName();
                    String messageText = message.getMessage();
                    String timestamp = message.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    setText(sender + ": " + messageText + " (" + timestamp + ")");
                }
            }
        });

        // Send message on pressing Enter key
        textFieldMessage.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleSendMessage();
            }
        });

        buttonSend.setOnAction(event -> handleSendMessage());
    }

    private void initModel() {
        Platform.runLater(() -> {
            messageModel.clear();
            // Obține mesajele dintre utilizatori
            messageService.getMessagesBetween(loggedInUser.getId(), friend.getId()).forEach(messageModel::add);
            listViewMessages.setItems(messageModel);
        });
    }

    private void handleSendMessage() {
        String messageText = textFieldMessage.getText().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(loggedInUser.getId(), friend.getId(), messageText, java.time.LocalDateTime.now());
            messageService.sendMessage(message);
            textFieldMessage.clear();
            initModel(); // Actualizezi lista după trimitere
        }
    }

    private void startMessagePolling() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            initModel(); // Actualizează mesajele
        }, 0, 2, TimeUnit.SECONDS);
    }

}
