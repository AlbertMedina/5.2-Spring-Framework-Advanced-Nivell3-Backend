package com.videostore.videostore.domain.model.user;

import com.videostore.videostore.domain.model.user.valueobject.*;

public class User {

    private final UserId id;
    private final Name name;
    private final Surname surname;
    private final Username username;
    private final Email email;
    private final Password password;
    private final Role role;

    private User(UserId id, Name name, Surname surname, Username username, Email email, Password password, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UserId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Surname getSurname() {
        return surname;
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public static User create(UserId id, Name name, Surname surname, Username username, Email email, Password password, Role role) {
        return new User(id, name, surname, username, email, password, role);
    }
}
