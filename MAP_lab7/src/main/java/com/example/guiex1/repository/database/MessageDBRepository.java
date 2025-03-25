package com.example.guiex1.repository.database;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.User;
import com.example.guiex1.domain.validators.MessageValidator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {

    private String url;
    private String username;
    private String password;
    private MessageValidator messageValidator;
    public MessageDBRepository(String url, String username, String password, MessageValidator messageValidator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    @Override
    public Optional<Message> findOne(Long aLong){
        String query = "SELECT * FROM messages WHERE \"id\" = ?";
        Message message = null;
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFrom = resultSet.getLong("idFrom");
                Long idTo = resultSet.getLong("idTo");
                String message_text = resultSet.getString("message_text");
                LocalDateTime send_time = resultSet.getTimestamp("send_time").toLocalDateTime();
                message = new Message(idFrom, idTo,message_text,send_time);
                message.setId(aLong);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(message);
    }


    @Override
    public Iterable<Message> findAll() {
        HashMap<Long, Message> messages = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement("select * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFrom = resultSet.getLong("idFrom");
                Long idTo = resultSet.getLong("idTo");
                String message_text = resultSet.getString("message_text");
                LocalDateTime send_time = resultSet.getTimestamp("send_time").toLocalDateTime();
                Message message = new Message(idFrom, idTo,message_text,send_time);
                message.setId(id);

                messages.put(message.getId(), message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messages.values();
    }

    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Message can't be null!");
        }
        String query = "INSERT INTO messages(\"idFrom\", \"idTo\", \"message_text\", \"send_time\") VALUES (?,?,?,?) RETURNING id";
        messageValidator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, entity.getFrom());
            statement.setLong(2, entity.getTo());
            statement.setString(3,entity.getMessage());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getData()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long generatedId = resultSet.getLong(1); // Obține ID-ul generat
                entity.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {

        String query = "DELETE FROM messages WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Message messageToDelete = null;
        for (Message message : findAll()) {
            if (Objects.equals(message.getId(), aLong)) {
                messageToDelete = message;
            }
        }
        return Optional.ofNullable(messageToDelete);
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }


}
