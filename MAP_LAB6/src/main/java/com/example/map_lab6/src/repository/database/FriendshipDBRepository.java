package com.example.map_lab6.src.repository.database;

import com.example.map_lab6.src.domain.Friendship;
import com.example.map_lab6.src.domain.validators.FriendshipValidator;
import com.example.map_lab6.src.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FriendshipDBRepository implements Repository<Long, Friendship> {

    FriendshipValidator friendshipValidator;

    public FriendshipDBRepository(FriendshipValidator friendshipValidator) {
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public Optional<Friendship> findOne(Long aLong) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/network", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("idfriend1");
                Long idFriend2 = resultSet.getLong("idfriend2");
                LocalDateTime friendshipDate = resultSet.getTimestamp("friendship_date").toLocalDateTime();
                friendship = new Friendship(idFriend1, idFriend2, friendshipDate);
                friendship.setId(aLong);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(friendship);
    }


    @Override
    public Iterable<Friendship> findAll() {
        Map<Long, Friendship> friendships = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/network", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("idfriend1");
                Long idFriend2 = resultSet.getLong("idfriend2");
                LocalDateTime friendshipDate = resultSet.getTimestamp("friendship_date").toLocalDateTime();
                Friendship friendship = new Friendship(idFriend1, idFriend2, friendshipDate);
                friendship.setId(id);
                friendships.put(friendship.getId(), friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendships.values();
    }


    @Override
    public Optional<Friendship> save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Friendship can't be null!");
        }
        String query = "INSERT INTO friendships(\"id\", \"idfriend1\", \"idfriend2\", \"friendship_date\") VALUES (?,?,?,?)";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/network", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getIdUser1());
            statement.setLong(3, entity.getIdUser2());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getDate())); // Convertește LocalDateTime la Timestamp
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }


    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM friendships WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/network", "postgres", "admin");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Friendship friendshipToDelete = null;
        for (Friendship friendship : findAll()) {
            if (Objects.equals(friendship.getId(), aLong)) {
                friendshipToDelete = friendship;
            }
        }
        return Optional.ofNullable(friendshipToDelete);
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        return Optional.empty();
    }
}