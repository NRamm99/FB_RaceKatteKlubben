package dk.race.racekatteklubben.domain.model;

import java.time.LocalDate;
import java.util.List;

public class User {
    int id;
    String username;
    String email;
    String passwordHash;
    LocalDate signUpDate;
    Role role;
    List<Pet> pets;

    public User(int id, String username, String email, String passwordHash, LocalDate signUpDate, List<Pet> pets) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.signUpDate = signUpDate;
        this.role = role;
        this.pets = pets;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPasswordHash() {return passwordHash;}

    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}

    public LocalDate getSignUpDate() {return signUpDate;}

    public void setSignUpDate(LocalDate signUpDate) {this.signUpDate = signUpDate;}

    public Role getRole() {return role;}

    public void setRole(Role role) {this.role = role;}

    public List<Pet> getPets() {return pets;}

    public void setPets(List<Pet> pets) {this.pets = pets;}
}