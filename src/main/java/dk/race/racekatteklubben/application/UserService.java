package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (userRepository.findByMail(user.getEmail()) != null) {
            throw new IllegalArgumentException("A user with that email already exists");
        }
    }

    public void editUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    public void deleteUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    public User findUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
