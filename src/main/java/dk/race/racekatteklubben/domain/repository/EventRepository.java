package dk.race.racekatteklubben.domain.repository;

import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;

import java.util.List;

public interface EventRepository {

    public void addEvent(Event event);

    public void updateEvent(Event event);

    public void removeEvent(Event event);

    public List<Event> getEvents();

    public List<Event> getUpcomingEventsByOwnerId(int ownerId);

    public Event getEventById(int id);

    public void addAttendingPet(int eventId, int petId);

    public void removeAttendingPet(int eventId, int petId);

    List<Pet> findAttendingPetsByEventId(int eventId);
}
