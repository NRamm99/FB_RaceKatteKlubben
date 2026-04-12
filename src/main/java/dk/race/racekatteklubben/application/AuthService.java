package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    public void register(User user, String rawPassword) {
        userValidator.validateUserForRegister(user, rawPassword);

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Brugernavnet er allerede i brug");
        }

        if (userRepository.findByMail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Der findes allerede en bruger med den e-mail");
        }

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    public User login(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Brugernavn må ikke være tomt");
        }

        userValidator.validateRawPassword(rawPassword);

        User user = userRepository.findByUsername(username.trim());
        if (user == null) {
            throw new IllegalArgumentException("Forkert brugernavn eller adgangskode");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Forkert brugernavn eller adgangskode");
        }

        return user;
    }
}