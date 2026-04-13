package dk.race.racekatteklubben.infrastructure;

import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.repository.EventRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class EventRepositoryImpl implements EventRepository {

    private final JdbcTemplate jdbcTemplate;

    public EventRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEvent(Event event) {
        String sql = """
                        INSERT INTO events (title, description, event_date)
                        VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                event.getTitle(),
                event.getDescription(),
                Timestamp.valueOf(event.getDateTime())
        );
    }

    @Override
    public void removeEvent(Event event) {
        String sql = """
                DELETE FROM events
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, event.getId());
    }

    @Override
    public List<Event> getEvents() {
        String sql = """
                SELECT id, title, description, event_date
                FROM events
                ORDER BY event_date ASC;
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToEvent(rs));
    }

    @Override
    public Event getEventById(int id) {
        String sql = """
                SELECT id, title, description, event_date
                FROM events
                WHERE id = ?;
                """;

        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return mapRowToEvent(rs);
            }
            return null;
        }, id);
    }

    @Override
    public List<Pet> findAttendingPetsByEventId(int eventId) {
        String sql = """
                SELECT p.id, p.name, p.race, p.owner_id
                FROM pets p
                JOIN pet_in_event pie ON p.id = pie.pet_id
                WHERE pie.event_id = ?
                ORDER BY p.id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToPet(rs), eventId);
    }

    @Override
    public void addAttendingPet(int petId, int eventId) {
        String sql = """
                INSERT INTO pet_in_event (pet_id, event_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, petId, eventId);
    }

    @Override
    public void removeAttendingPet(int petId, int eventId) {
        String sql = """
                DELETE FROM pet_in_event
                WHERE pet_id = ? AND event_id = ?;
                """;

        jdbcTemplate.update(sql, petId, eventId);
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("event_date").toLocalDateTime()
        );
    }

    private Pet mapRowToPet(ResultSet rs) throws SQLException {
        return new Pet(
                rs.getInt("id"),
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("owner_id")
        );
    }
}
