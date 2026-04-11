package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public void createPet(Pet pet) {
        petRepository.save(pet);
    }

    public void updatePet(Pet pet) {
        petRepository.update(pet);
    }

    public void deletePet(int id) {
        petRepository.deleteById(id);
    }

    public Pet getPetById(int id) {
        return petRepository.findById(id);
    }

    public List<Pet> getPetsByOwnerId(int ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Oplysninger om katten mangler");
        }

        if (pet.getName() == null || pet.getName().isBlank()) {
            throw new IllegalArgumentException("Katten skal have et navn");
        }

        if (pet.getRace() == null) {
            throw new IllegalArgumentException("Katten skal have en race");
        }

        if (pet.getOwnerId() <= 0) {
            throw new IllegalArgumentException("Katten skal være knyttet til en ejer");
        }
    }
}
