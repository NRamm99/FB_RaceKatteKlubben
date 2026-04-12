package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
}