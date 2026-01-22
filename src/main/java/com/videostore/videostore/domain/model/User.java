package com.videostore.videostore.domain.model;

public class User {

    private final String fullName;
    private final String username;
    private final String email;

    private User(String fullName, String username, String email) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
