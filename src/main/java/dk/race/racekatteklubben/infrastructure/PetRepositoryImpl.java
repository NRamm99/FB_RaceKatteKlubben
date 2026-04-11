package dk.race.racekatteklubben.infrastructure;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;

@Repository
public class PetRepositoryImpl implements PetRepository {

    private final JdbcTemplate jdbcTemplate;

    public PetRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Pet pet){
        String sql = """
                INSERT INTO pets (name, race, owner_id)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                pet.getName(),
                pet.getRace().name(),
                pet.getOwnerId());
    }

    @Override
    public void update(Pet pet){
        String sql = """
                UPDATE pets SET name = ?, race = ?, owner_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(
                sql,
                pet.getName(),
                pet.getRace().name(),
                pet.getOwnerId(),
                pet.getId());

    }

    @Override
    public void deleteById(int id){
        String sql = """
                DELETE FROM pets
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, id);
    }

    @Override
    public Pet findById(int id){
        String sql = """
                SELECT id, name, race, owner_id
                FROM pets
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            if(rs.next()){
                return mapRowToPet(rs);
            }
            return null;
        }, id);

    }

    @Override
    public List<Pet> findAll(){
        String sql = """
                SELECT id, name, race, owner_id
                FROM pets
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToPet(rs));
    }

    @Override
    public List<Pet> findByOwnerId(int ownerId){
        String sql = """
                SELECT id, name, race, owner_id
                FROM pets
                WHERE owner_id = ?
                ORDER BY id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToPet(rs), ownerId);
    }

    private Pet mapRowToPet(ResultSet rs) throws SQLException{
        return  new Pet(
                rs.getInt("id"),
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("owner_id")
        );
    }

}
