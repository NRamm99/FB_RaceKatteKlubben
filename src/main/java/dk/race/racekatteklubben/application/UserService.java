package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.application.validation.UserValidator;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public void editUser(User user, String rawPassword) {
        userValidator.validateUserForWrite(user);
        userValidator.validateRawPassword(rawPassword);

        if (user.getId() <= 0) {
            throw new IllegalArgumentException("Ugyldig bruger");
        }
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.update(user);
    }

    public User updateProfile(User currentUser, String username, String email) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Brugeren er ikke logget ind");
        }

        if (currentUser.getId() <= 0) {
            throw new IllegalArgumentException("Ugyldig bruger");
        }

        currentUser.changeUsername(username);
        currentUser.changeEmail(email);

        userValidator.validateUserForWrite(currentUser);

        User existingByUsername = userRepository.findByUsername(currentUser.getUsername());
        if (existingByUsername != null && existingByUsername.getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Brugernavnet er allerede i brug");
        }

        User existingByMail = userRepository.findByMail(currentUser.getEmail());
        if (existingByMail != null && existingByMail.getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Der findes allerede en bruger med den e-mail");
        }

        userRepository.update(currentUser);
        return currentUser;
    }

    public void changePassword(User currentUser, String oldPassword, String newPassword, String repeatPassword) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Brugeren er ikke logget ind");
        }

        if (currentUser.getId() <= 0) {
            throw new IllegalArgumentException("Ugyldig bruger");
        }

        userValidator.validateRawPassword(oldPassword);
        userValidator.validateRawPassword(newPassword);
        userValidator.validateRawPassword(repeatPassword);

        if (!passwordEncoder.matches(oldPassword, currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Nuværende adgangskode er forkert");
        }

        if (!newPassword.equals(repeatPassword)) {
            throw new IllegalArgumentException("De nye adgangskoder matcher ikke");
        }

        if (oldPassword.equals(newPassword)) {
            throw new IllegalArgumentException("Den nye adgangskode skal være forskellig fra den gamle");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.update(currentUser);
    }

    public void deleteUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Oplysninger om brugeren mangler");
        }

        if (user.getId() <= 0) {
            throw new IllegalArgumentException("Ugyldig bruger");
        }

        userRepository.delete(user);
    }

    public User findUserByMail(String mail) {
        if (mail == null || mail.isBlank()) {
            throw new IllegalArgumentException("E-mail må ikke være tom");
        }

        return userRepository.findByMail(mail.trim().toLowerCase());
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Ugyldig bruger");
        }

        return userRepository.findAll().stream()
                .filter(user -> user != null && user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Brugeren blev ikke fundet"));
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.isBlank()) {
            return findAllUsers();
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);

        return userRepository.findAll().stream()
                .filter(user -> user != null)
                .filter(user ->
                        user.getUsername().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                                || user.getEmail().toLowerCase(Locale.ROOT).contains(normalizedQuery))
                .toList();
    }
}
