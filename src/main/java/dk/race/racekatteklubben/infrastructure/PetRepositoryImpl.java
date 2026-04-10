package dk.race.racekatteklubben.infrastructure;

import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PetRepositoryImpl implements PetRepository {

    @Override
    public void save(Pet pet){}

    @Override
    public void update(Pet pet){}

    @Override
    public void deleteById(int id){}

    @Override
    public Pet findById(int id){
        return null;
    }

    @Override
    public List<Pet> findByOwnerId(int ownerId){
        return new ArrayList<>();
    }
    @Override
    public List<Pet> findAll(){
        return new ArrayList<>();
    }

}
