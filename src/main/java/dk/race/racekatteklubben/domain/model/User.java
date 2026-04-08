package dk.race.racekatteklubben.domain.model;

import java.time.LocalDate;

public class User {
    String name;
    String email;
    String passwordHash;
    LocalDate signUpDate;
    // Role role;
    // List<Pet> pets;

    public String getName() {
        return name;
    }
}