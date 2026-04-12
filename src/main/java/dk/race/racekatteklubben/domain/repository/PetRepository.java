package dk.race.racekatteklubben.domain.repository;

import dk.race.racekatteklubben.domain.model.Pet;

import java.util.List;

public interface PetRepository {
    void save(Pet pet);
    void update(Pet pet);
    void deleteById(int id);
    Pet findById(int id);
    List<Pet> findAll();
    List<Pet> findByOwnerId(int ownerId);

}
