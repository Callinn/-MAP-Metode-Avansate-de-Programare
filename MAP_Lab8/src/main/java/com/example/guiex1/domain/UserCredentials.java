package com.example.guiex1.domain;

public class UserCredentials extends Entity<Long> {
    private Integer userId;
    private String email;
    private String password;

    public UserCredentials(Integer userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                '}';
    }
}
