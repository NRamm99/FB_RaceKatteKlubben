package dk.race.racekatteklubben.domain.model;

public class Pet {
    int id;
    String name;
    Race race;
    int ownerId;

    public Pet(int id, String name, Race race, int ownerId) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {this.race = race;}

    public int getOwnerId() {return ownerId;}

    public void setOwnerId(int ownerId) {this.ownerId = ownerId;}
}
