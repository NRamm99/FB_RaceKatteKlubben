package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(dk|com|net|org|edu|io|info|eu|app|dev|co\\.uk)$");

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        validateUserForWrite(user);

        if (userRepository.findByMail(user.getEmail()) != null) {
            throw new IllegalArgumentException("En brugere med den mail eksisterer allerede");
        }

        userRepository.save(user);
    }

    public void editUser(User user) {
        validateUserForWrite(user);
        userRepository.update(user);
    }

    public void deleteUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Navn kan ikke være tomt");
        }

        userRepository.delete(user);
    }

    public User findUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    private void validateUserForWrite(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Navn kan ikke være tomt");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email kan ikke vaere tomt");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Email skal vaere gyldig");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Adgangskode kan ikke være tom");
        }
    }
}
