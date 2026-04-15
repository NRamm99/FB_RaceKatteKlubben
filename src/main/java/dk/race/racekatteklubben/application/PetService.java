package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void createPet(Pet pet) {
        validatePet(pet);
        normalizePetName(pet);
        petRepository.save(pet);
    }

    public void updatePet(Pet pet) {
        validatePet(pet);

        Pet existingPet = petRepository.findById(pet.getId());
        if (existingPet == null) {
            throw new IllegalArgumentException("Katten blev ikke fundet");
        }

        normalizePetName(pet);
        petRepository.update(pet);
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

    /**
     * Owner lookup map for views: owner_id -> username.
     */
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

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Oplysninger om katten mangler");
        }

        if (pet.getName() == null || pet.getName().isBlank()) {
            throw new IllegalArgumentException("Katten skal have et navn");
        }

        String normalizedName = pet.getName().trim().replaceAll("\\s+", " ");

        if (!normalizedName.matches("^[A-Za-zÆØÅæøå\\- ]+$")){
            throw new IllegalArgumentException("Kattens navn må kun indeholde bogstaver, mellemrum og bindestreg");
        }

        if (normalizedName.contains("--")){
            throw new IllegalArgumentException("Kattens navn må ikke indeholde flere bindestreger i træk");
        }

        if (normalizedName.startsWith("-")){
            throw new IllegalArgumentException("Kattens navn må ikke starte med bindestreg");
        }

        if (normalizedName.endsWith("-")){
            throw new IllegalArgumentException("Kattens navn må ikke ende med bindestreg");
        }

        if (pet.getRace() == null) {
            throw new IllegalArgumentException("Katten skal have en race");
        }

        if (pet.getOwnerId() <= 0) {
            throw new IllegalArgumentException("Katten skal være knyttet til en gyldig ejer");
        }
    }

    private void normalizePetName(Pet pet) {
        String normalizedName = pet.getName().trim().replaceAll("\\s+", " ");
        pet.changeName(normalizedName);
    }
}
