package dk.race.racekatteklubben.application.validation;

import dk.race.racekatteklubben.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(dk|com|net|org|edu|io|info|eu|app|dev|co\\.uk)$");

    public void validateUserForWrite(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Oplysninger om brugeren mangler");
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Brugernavn må ikke være tomt");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail må ikke være tom");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("E-mailadressen er ikke gyldig");
        }
    }

    public void validateRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Adgangskode kan ikke være tom");
        }
    }

    public void validatePasswordHash(User user) {
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Adgangskode kan ikke være tom");
        }
    }

    public void validateUserForRegister(User user, String rawPassword) {
        validateUserForWrite(user);
        validateRawPassword(rawPassword);
    }
}
