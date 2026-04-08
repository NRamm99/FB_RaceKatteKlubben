package dk.race.racekatteklubben.domain.model;

public class Pet {
    int id;
    String name;
    Race race;
    String ownerMail;

    public Pet(int id, String name, Race race, String ownerMail) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.ownerMail = ownerMail;
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
}
