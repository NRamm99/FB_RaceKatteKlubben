package dk.race.racekatteklubben.infrastructure;

import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import dk.race.racekatteklubben.infrastructure.DatabaseConfig;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    @Override
    public void save(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User findByMail(String mail) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<User> findAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
