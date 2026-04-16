package dk.race.racekatteklubben.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int id;
    private int ownerId;
    private String title;
    private String description;

    private List<Pet> attendingPets;
    private LocalDateTime dateTime;

    public Event(int id, int ownerId, String title, String description, LocalDateTime dateTime) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.attendingPets = new ArrayList<>();
        this.dateTime = dateTime;
    }

    public List<Pet> getAttendingPets() {
        return attendingPets;
    }

    public void replaceAttendingPets(List<Pet> attendingPets) {
        this.attendingPets = new ArrayList<>(attendingPets);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void reschedule(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void addAttendingPet(Pet pet) {
        this.attendingPets.add(pet);
    }

    public void removeAttendingPet(Pet pet) {
        this.attendingPets.remove(pet);
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void retitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void reviseDescription(String description) {
        this.description = description;
    }
}
