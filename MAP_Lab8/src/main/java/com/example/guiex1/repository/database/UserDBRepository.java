package com.example.guiex1.repository.database;

import com.example.guiex1.domain.User;
import com.example.guiex1.domain.validators.UserValidator;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.UserRepository;
import com.example.guiex1.util.paging.Page;
import com.example.guiex1.util.paging.Pageable;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements UserRepository {
    private String url;
    private String username;
    private String password;
    UserValidator userValidator;

    public UserDBRepository(String url, String username, String password,UserValidator userValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userValidator = userValidator;
    }


    @Override
    public Optional<User> findOne(Long aLong) {
        String query = "select * from users WHERE \"id\" = ?";
        User user = null;
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {

            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                user = new User(firstName, lastName);
                user.setId(aLong);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        HashMap<Long, User> users = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String nume = resultSet.getString("first_name");
                String prenume = resultSet.getString("last_name");
                User user = new User(nume, prenume);
                user.setId(id);

                users.put(user.getId(), user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users.values();
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("User can't be null!");
        }
        String query = "INSERT INTO users(\"first_name\", \"last_name\") VALUES (?,?) RETURNING id";
        userValidator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
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
    public Optional<User> delete(Long aLong) {

        String query = "DELETE FROM users WHERE \"id\" = ?";

        try (Connection connection = DriverManager.getConnection(url, username,password);
             PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        User userToDelete = null;
        for (User user : findAll()) {
            if (Objects.equals(user.getId(), aLong)) {
                userToDelete = user;
            }
        }
        return Optional.ofNullable(userToDelete);
    }

    @Override
    public Optional<User> update(User entity) {
        return Optional.empty();
    }


    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        String query = "SELECT * FROM users LIMIT ? OFFSET ?";
        List<User> usersOnPage = new ArrayList<>();
        int totalUsers = 0;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Determină numărul total de utilizatori
            String countQuery = "SELECT COUNT(*) AS total FROM users";
            try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
                 ResultSet countResult = countStatement.executeQuery()) {
                if (countResult.next()) {
                    totalUsers = countResult.getInt("total");
                }
            }

            // Obține utilizatorii pentru pagina curentă
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, pageable.getPageSize()); // LIMIT
                statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber()); // OFFSET
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");

                    User user = new User(firstName, lastName);
                    user.setId(id);
                    usersOnPage.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Page<>(usersOnPage, totalUsers);

    }

}