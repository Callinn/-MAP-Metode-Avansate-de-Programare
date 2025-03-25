package com.example.guiex1.repository.database;

import com.example.guiex1.domain.UserCredentials;
import com.example.guiex1.domain.validators.CredentialsValidator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class UserCredentialsDBRepository implements Repository<Long, UserCredentials> {
        private String url;
        private String username;
        private String password;
        CredentialsValidator credentialsValidator;

        public UserCredentialsDBRepository(String url, String username, String password, CredentialsValidator credentialsValidator) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.credentialsValidator = credentialsValidator;
        }


        @Override
        public Optional<UserCredentials> findOne(Long aLong) {
            String query = "select * from usercredentials WHERE \"id\" = ?";
            UserCredentials userCredentials = null;
            try (Connection connection = DriverManager.getConnection(url,username,password);
                 PreparedStatement statement = connection.prepareStatement(query);) {

                statement.setLong(1, aLong);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Integer userId = resultSet.getInt("iduser");
                    userCredentials = new UserCredentials(userId,email, password);
                    userCredentials.setId(aLong);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Optional.ofNullable(userCredentials);
        }

        @Override
        public Iterable<UserCredentials> findAll() {
            HashMap<Long, UserCredentials> usersCredentials = new HashMap<>();
            try (Connection connection = DriverManager.getConnection(url,username,password);
                 PreparedStatement statement = connection.prepareStatement("select * from usercredentials");
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    Integer userId = resultSet.getInt("iduser");
                    UserCredentials userCredentials = new UserCredentials(userId,email, password);
                    userCredentials.setId(id);

                    usersCredentials.put(userCredentials.getId(), userCredentials);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return usersCredentials.values();
        }

        @Override
        public Optional<UserCredentials> save(UserCredentials entity) {
            if (entity == null) {
                throw new IllegalArgumentException("UserCredentials can't be null!");
            }
            String query = "INSERT INTO usercredentials(\"email\", \"password\",\"iduser\") VALUES (?,?,?) RETURNING id";
            credentialsValidator.validate(entity);
            try (Connection connection = DriverManager.getConnection(url,username,password);
                 PreparedStatement statement = connection.prepareStatement(query);) {
                statement.setString(1, entity.getEmail());
                statement.setString(2, entity.getPassword());
                statement.setInt(3,entity.getUserId());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong(1); // Obține ID-ul generat
                    entity.setId(generatedId);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return Optional.of(entity); // Return the saved entity

        }

        @Override
        public Optional<UserCredentials> delete(Long aLong) {

            String query = "DELETE FROM usercredentials WHERE \"id\" = ?";

            try (Connection connection = DriverManager.getConnection(url, username,password);
                 PreparedStatement statement = connection.prepareStatement(query);) {
                statement.setLong(1, aLong);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            UserCredentials userToDelete = null;
            for (UserCredentials user : findAll()) {
                if (Objects.equals(user.getId(), aLong)) {
                    userToDelete = user;
                }
            }
            return Optional.ofNullable(userToDelete);
        }

    @Override
        public Optional<UserCredentials> update(UserCredentials entity) {
            return Optional.empty();
        }
    }

