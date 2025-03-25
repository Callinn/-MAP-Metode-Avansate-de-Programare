package com.example.guiex1.repository.database;

import com.example.guiex1.domain.Friendship;
import com.example.guiex1.domain.validators.FriendshipValidator;
import com.example.guiex1.repository.FriendshipRepository;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDate;
import java.util.*;


public class FriendshipDBRepository implements FriendshipRepository {
    private String url;
    private String username;
    private String password;
    FriendshipValidator friendshipValidator;

    public FriendshipDBRepository(String url, String username, String password,FriendshipValidator friendshipValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.friendshipValidator = friendshipValidator;
    }

    @Override
    public Optional<Friendship> findOne(Long aLong) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship = null;
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idFriend1 = resultSet.getLong("idfriend1");
                Long idFriend2 = resultSet.getLong("idfriend2");
                LocalDate friendshipDate = resultSet.getDate("friendship_date").toLocalDate();
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
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("idfriend1");
                Long idFriend2 = resultSet.getLong("idfriend2");
                LocalDate friendshipDate = resultSet.getDate("friendship_date").toLocalDate();
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
        String query = "INSERT INTO friendships(\"idfriend1\", \"idfriend2\", \"friendship_date\") VALUES (?,?,?)";

        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getIdUser1());
            statement.setLong(2, entity.getIdUser2());
            statement.setDate(3, Date.valueOf(entity.getDate()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long generatedId = resultSet.getLong(1); // Obține ID-ul generat
                entity.setId(generatedId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }


    @Override
    public Optional<Friendship> delete(Long aLong) {
        String query = "DELETE FROM friendships WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url,username,password);
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




    public long count() {
        String query = "SELECT COUNT(*) AS total FROM friendships";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        List<Friendship> friendships = new ArrayList<>();
        String query = "SELECT * FROM friendships ORDER BY id LIMIT ? OFFSET ?";
        long totalElements = count();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idFriend1 = resultSet.getLong("idfriend1");
                Long idFriend2 = resultSet.getLong("idfriend2");
                LocalDate friendshipDate = resultSet.getDate("friendship_date").toLocalDate();

                Friendship friendship = new Friendship(idFriend1, idFriend2, friendshipDate);
                friendship.setId(id);
                friendships.add(friendship);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Page<>(friendships, (int) totalElements);
    }

}