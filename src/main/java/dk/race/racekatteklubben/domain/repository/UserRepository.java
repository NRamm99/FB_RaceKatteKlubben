package dk.race.racekatteklubben.domain.repository;

import dk.race.racekatteklubben.domain.model.User;

import java.util.List;

public interface UserRepository {

    void save(User user);

    void update(User user);

    void delete(User user);

    User findByMail(String mail);

    User findByUsername(String username);

    List<User> findAll();
}
