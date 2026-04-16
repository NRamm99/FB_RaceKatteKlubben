package dk.race.racekatteklubben.domain.model;

public class Pet {
    private int id;
    private String name;
    private Race race;
    private int ownerId;

    public Pet(int id, String name, Race race, int ownerId) {
        this.id = id;
        this.name = normalizeName(name);
        validateName(this.name);
        validateRace(race);
        validateOwnerId(ownerId);
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
        String normalizedName = normalizeName(name);
        validateName(normalizedName);
        this.name = normalizedName;
    }

    public Race getRace() {
        return race;
    }

    public void changeRace(Race race) {
        validateRace(race);
        this.race = race;
    }

    public int getOwnerId() {
        return ownerId;
    }

    private static String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Katten skal have et navn");
        }

        return name.trim().replaceAll("\\s+", " ");
    }

    private static void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Katten skal have et navn");
        }

        if (!name.matches("^[A-Za-zÆØÅæøå\\- ]+$")) {
            throw new IllegalArgumentException("Kattens navn må kun indeholde bogstaver, mellemrum og bindestreg");
        }

        if (name.contains("--")) {
            throw new IllegalArgumentException("Kattens navn må ikke indeholde flere bindestreger i træk");
        }

        if (name.startsWith("-")) {
            throw new IllegalArgumentException("Kattens navn må ikke starte med bindestreg");
        }

        if (name.endsWith("-")) {
            throw new IllegalArgumentException("Kattens navn må ikke ende med bindestreg");
        }
    }

    private static void validateRace(Race race) {
        if (race == null) {
            throw new IllegalArgumentException("Katten skal have en race");
        }
    }

    private static void validateOwnerId(int ownerId) {
        if (ownerId <= 0) {
            throw new IllegalArgumentException("Katten skal være knyttet til en gyldig ejer");
        }
    }
}
