package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerRejectsInvalidEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(repository, passwordEncoder, userValidator);

        User user = createUser("invalid-email");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(user, "hemmeligKode123")
        );

        assertEquals("E-mailadressen er ikke gyldig", exception.getMessage());
        assertTrue(repository.savedUsers.isEmpty());
    }

    @Test
    void registerSavesValidUser() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(repository, passwordEncoder, userValidator);

        User user = createUser("cat.owner@example.com");

        assertDoesNotThrow(() -> authService.register(user, "hemmeligKode123"));

        assertEquals(1, repository.savedUsers.size());

        User savedUser = repository.savedUsers.get(0);
        assertEquals("cat.owner@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getPasswordHash());
        assertNotEquals("hemmeligKode123", savedUser.getPasswordHash());
    }

    @Test
    void editUserUpdatesRepository() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(repository, userValidator, passwordEncoder);
        User user = createUser("cat.owner@example.com");

        assertDoesNotThrow(() -> userService.editUser(user, "hemmeligKode123"));

        assertEquals(1, repository.updatedUsers.size());
        assertEquals(user, repository.updatedUsers.get(0));
    }

    @Test
    void deleteUserDeletesRepositoryEntry() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(repository, userValidator,  passwordEncoder);
        User user = createUser("cat.owner@example.com");

        assertDoesNotThrow(() -> userService.deleteUser(user));

        assertEquals(1, repository.deletedUsers.size());
        assertEquals(user, repository.deletedUsers.get(0));
    }

    @Test
    void findUserByMailReturnsMatchingUser() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(repository, userValidator, passwordEncoder);
        User user = createUser("cat.owner@example.com");
        repository.save(user);

        User foundUser = userService.findUserByMail("cat.owner@example.com");

        assertEquals(user, foundUser);
    }

    @Test
    void findAllUsersReturnsAllSavedUsers() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserValidator userValidator = new UserValidator();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserService userService = new UserService(repository, userValidator, passwordEncoder);
        User firstUser = createUser("first@example.com");
        User secondUser = new User(
                2,
                "secondowner",
                "second@example.com",
                "another-password",
                LocalDate.now(),
                List.of()
        );
        repository.save(firstUser);
        repository.save(secondUser);

        List<User> users = userService.findAllUsers();

        assertEquals(2, users.size());
        assertEquals(firstUser, users.get(0));
        assertEquals(secondUser, users.get(1));
    }

    private static User createUser(String email) {
        return new User(
                1,
                "catowner",
                email,
                "hashed-password",
                LocalDate.now(),
                List.of()
        );
    }

    private static final class InMemoryUserRepository implements UserRepository {
        private final List<User> savedUsers = new ArrayList<>();
        private final List<User> updatedUsers = new ArrayList<>();
        private final List<User> deletedUsers = new ArrayList<>();

        @Override
        public void save(User user) {
            savedUsers.add(user);
        }

        @Override
        public void update(User user) {
            updatedUsers.add(user);
        }

        @Override
        public void delete(User user) {
            deletedUsers.add(user);
        }

        @Override
        public User findByMail(String mail) {
            return savedUsers.stream()
                    .filter(user -> user.getEmail().equals(mail))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public User findByUsername(String username) {
            return null;
        }

        @Override
        public List<User> findAll() {
            return List.copyOf(savedUsers);
        }
    }
}
