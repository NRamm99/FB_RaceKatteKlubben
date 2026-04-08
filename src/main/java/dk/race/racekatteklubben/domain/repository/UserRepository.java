package dk.race.racekatteklubben.domain.repository;

import dk.race.racekatteklubben.domain.model.User;

import java.util.List;

public interface UserRepository {

    public void save(User user);

    public void update(User user);

    public void delete(User user);

    public User findByMail(String mail);

    public List<User> findAll();
}
