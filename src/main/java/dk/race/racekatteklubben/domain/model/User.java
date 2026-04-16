package dk.race.racekatteklubben.domain.model;

import java.time.LocalDate;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDate signUpDate;
    private Role role;

    public User(int id, String username, String email, String passwordHash, LocalDate signUpDate, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.signUpDate = signUpDate;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void assignId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void changeUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDate getSignUpDate() {
        return signUpDate;
    }

    public Role getRole() {
        return role;
    }
}
