package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetServiceTest {

    @Test
    void searchPetsMatchesNameAndRaceIgnoringCase() {
        InMemoryPetRepository repository = new InMemoryPetRepository();
        PetService petService = new PetService(repository);
        Pet firstPet = new Pet(1, "Luna", Race.MAINE_COON, 1);
        Pet secondPet = new Pet(2, "Bella", Race.BRITISH_SHORTHAIR, 2);
        repository.save(firstPet);
        repository.save(secondPet);

        List<Pet> nameMatches = petService.searchPets("luna");
        List<Pet> raceMatches = petService.searchPets("british");

        assertEquals(1, nameMatches.size());
        assertEquals(firstPet, nameMatches.get(0));
        assertEquals(1, raceMatches.size());
        assertEquals(secondPet, raceMatches.get(0));
    }

    private static final class InMemoryPetRepository implements PetRepository {
        private final List<Pet> pets = new ArrayList<>();

        @Override
        public void save(Pet pet) {
            pets.add(pet);
        }

        @Override
        public void update(Pet pet) {
        }

        @Override
        public void deleteById(int id) {
        }

        @Override
        public Pet findById(int id) {
            return pets.stream()
                    .filter(pet -> pet.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Pet> findAll() {
            return List.copyOf(pets);
        }

        @Override
        public List<Pet> findByOwnerId(int ownerId) {
            return pets.stream()
                    .filter(pet -> pet.getOwnerId() == ownerId)
                    .toList();
        }
    }
}
