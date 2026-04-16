package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void createPet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Oplysninger om katten mangler");
        }

        petRepository.save(pet);
    }

    public void createPetForOwner(String name, Race race, int ownerId) {
        createPet(new Pet(0, name, race, ownerId));
    }

    public void updatePet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Oplysninger om katten mangler");
        }

        Pet existingPet = petRepository.findById(pet.getId());
        if (existingPet == null) {
            throw new IllegalArgumentException("Katten blev ikke fundet");
        }

        petRepository.update(pet);
    }

    public void updatePetForOwner(int petId, String name, Race race, int ownerId) {
        Pet pet = getPetById(petId);

        if (pet.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Du ejer ikke denne kat");
        }

        pet.changeName(name);
        pet.changeRace(race);
        updatePet(pet);
    }

    public void deletePet(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Ugyldig kat");
        }

        Pet existingPet = petRepository.findById(id);
        if (existingPet == null) {
            throw new IllegalArgumentException("Katten blev ikke fundet");
        }

        petRepository.deleteById(id);
    }

    public void deletePetForOwner(int petId, int ownerId) {
        Pet pet = getPetById(petId);

        if (pet.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Du ejer ikke denne kat");
        }

        deletePet(petId);
    }

    public Pet getPetById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Ugyldigt kattenummer");
        }

        Pet pet = petRepository.findById(id);
        if (pet == null) {
            throw new IllegalArgumentException("Katten blev ikke fundet");
        }

        return pet;
    }

    public List<Pet> getPetsByOwnerId(int ownerId) {
        if (ownerId <= 0) {
            throw new IllegalArgumentException("Ugyldigt ejer-id");
        }

        return petRepository.findByOwnerId(ownerId);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> searchPets(String query) {
        if (query == null || query.isBlank()) {
            return getAllPets();
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);

        return petRepository.findAll().stream()
                .filter(pet -> pet != null)
                .filter(pet ->
                        pet.getName().toLowerCase(Locale.ROOT).contains(normalizedQuery)
                                || pet.getRace().name().toLowerCase(Locale.ROOT).replace('_', ' ').contains(normalizedQuery))
                .toList();
    }

    public Map<Integer, String> getOwnerIdToUsernameMap(List<User> users) {
        if (users == null) {
            throw new IllegalArgumentException("Users list is missing");
        }

        Map<Integer, String> ownerIdToUsername = new HashMap<>();
        for (User u : users) {
            if (u != null) {
                ownerIdToUsername.put(u.getId(), u.getUsername());
            }
        }
        return ownerIdToUsername;
    }
}
