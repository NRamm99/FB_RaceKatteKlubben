package dk.race.racekatteklubben.domain.model;

public class Pet {
    private int id;
    private String name;
    private Race race;
    private int ownerId;

    public Pet(int id, String name, Race race, int ownerId) {
        this.id = id;
        this.name = name;
        this.race = race;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Race getRace() {
        return race;
    }

    public void changeRace(Race race) {
        this.race = race;
    }

    public int getOwnerId() {
        return ownerId;
    }
}
