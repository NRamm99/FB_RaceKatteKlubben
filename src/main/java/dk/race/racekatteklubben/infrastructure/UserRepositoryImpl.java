package dk.race.racekatteklubben.infrastructure;

import dk.race.racekatteklubben.domain.model.Role;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(User user) {
        String sql = """
                INSERT INTO users (username, email, password_hash, sign_up_date, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());

            LocalDate signUpDate = user.getSignUpDate();
            ps.setDate(4, signUpDate != null ? Date.valueOf(signUpDate) : null);

            ps.setString(5, user.getRole() != null ? user.getRole().name() : null);

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void update(User user) {
        String sql = """
                UPDATE users
                SET username = ?, email = ?, password_hash = ?, sign_up_date = ?, role = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getSignUpDate() != null ? Date.valueOf(user.getSignUpDate()) : null,
                user.getRole() != null ? user.getRole().name() : null,
                user.getId()
        );
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    @Override
    public User findByMail(String mail) {
        String sql = """
                SELECT id, username, email, password_hash, sign_up_date, role
                FROM users
                WHERE email = ?
                """;

        List<User> result = jdbcTemplate.query(sql, userRowMapper(), mail);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public User findByUsername(String username) {
        String sql = """
            SELECT id, username, email, password_hash, sign_up_date, role
            FROM users
            WHERE username = ?
            """;

        List<User> result = jdbcTemplate.query(sql, userRowMapper(), username);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<User> findAll() {
        String sql = """
                SELECT id, username, email, password_hash, sign_up_date, role
                FROM users
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, userRowMapper());
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            Date signUpDate = rs.getDate("sign_up_date");

            String roleValue = rs.getString("role");
            Role role = null;

            if (roleValue != null && !roleValue.isBlank()) {
                role = Role.valueOf(roleValue);
            }

            return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    signUpDate != null ? signUpDate.toLocalDate() : null,
                    role,
                    List.of()
            );
        };
    }
}