package com.example.guiex1.repository.database;

import com.example.guiex1.domain.FriendRequest;
import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.validators.FriendRequestValidator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FriendRequestDBRepository implements Repository<Long, FriendRequest> {
    private String url;
    private String username;
    private String password;
    private FriendRequestValidator validator;

    public FriendRequestDBRepository(String url, String username, String password, FriendRequestValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> findOne(Long id) {
        String query = "SELECT * FROM friend_requests WHERE \"id\" = ?";
        FriendRequest friendRequest = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long senderId = resultSet.getLong("sender_id");
                Long receiverId = resultSet.getLong("receiver_id");
                LocalDate dateSent = resultSet.getDate("date_sent").toLocalDate();
                friendRequest = new FriendRequest(senderId, receiverId, dateSent);
                friendRequest.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendRequest);
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Map<Long, FriendRequest> requests = new HashMap<>();
        String query = "SELECT * FROM friend_requests";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long senderId = resultSet.getLong("sender_id");
                Long receiverId = resultSet.getLong("receiver_id");
                LocalDate dateSent = resultSet.getDate("date_sent").toLocalDate();
                FriendRequest friendRequest = new FriendRequest(senderId, receiverId, dateSent);
                friendRequest.setId(id);
                requests.put(id, friendRequest);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return requests.values();
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("FriendRequest can't be null!");
        }

        String query = "INSERT INTO friend_requests(\"sender_id\", \"receiver_id\", \"date_sent\") VALUES ( ?, ?, ?) RETURNING id";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getSenderId());
            statement.setLong(2, entity.getReceiverId());
            statement.setDate(3, Date.valueOf(entity.getDateSent()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long generatedId = resultSet.getLong(1); // Obține ID-ul generat
                entity.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving FriendRequest", e);
        }

        return Optional.empty();
    }



    @Override
    public Optional<FriendRequest> delete(Long id) {
        String query = "DELETE FROM friend_requests WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        FriendRequest requestToDelete = null;
        for (FriendRequest friendRequest : findAll()) {
            if (Objects.equals(friendRequest.getId(), id)) {
                requestToDelete = friendRequest;
            }
        }
        return Optional.ofNullable(requestToDelete);
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        return Optional.empty();
    }
}
