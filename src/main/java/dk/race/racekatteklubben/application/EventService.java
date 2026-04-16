package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event) {
        validateEvent(event);
        eventRepository.addEvent(event);
    }

    public void updateEvent(Event event) {
        validateEvent(event);

        Event existingEvent = eventRepository.getEventById(event.getId());
        if (existingEvent == null) {
            throw new IllegalArgumentException("Begivenheden blev ikke fundet");
        }

        if (existingEvent.getOwnerId() != event.getOwnerId()) {
            throw new IllegalArgumentException("Du ejer ikke denne begivenhed");
        }

        eventRepository.updateEvent(event);
    }

    public void removeEvent(Event event) {
        Event existingEvent = eventRepository.getEventById(event.getId());
        if (existingEvent == null) {
            throw new IllegalArgumentException("Begivenheden blev ikke fundet");
        }

        if (existingEvent.getOwnerId() != event.getOwnerId()) {
            throw new IllegalArgumentException("Du ejer ikke denne begivenhed");
        }

        eventRepository.removeEvent(event);
    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }

    public List<Event> getUpcomingEventsByOwnerId(int ownerId) {
        if (ownerId <= 0) {
            throw new IllegalArgumentException("Ugyldigt ejer-id");
        }

        return eventRepository.getUpcomingEventsByOwnerId(ownerId);
    }

    public void addAttendingPet(int eventId, int petId) {
        eventRepository.addAttendingPet(petId, eventId);
    }

    public void removeAttendingPet(int eventId, int petId) {
        eventRepository.removeAttendingPet(petId, eventId);
    }

    public Event getEventById(int id) {
        Event event = eventRepository.getEventById(id);

        if (event != null) {
            List<Pet> pets = eventRepository.findAttendingPetsByEventId(id);
            event.replaceAttendingPets(pets);
        }

        return event;
    }

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Arrangementet mangler");
        }

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("Arrangementet skal have en titel");
        }

        if (event.getDescription() == null || event.getDescription().isBlank()) {
            throw new IllegalArgumentException("Arrangementet skal have en beskrivelse");
        }

        if (event.getDateTime() == null) {
            throw new IllegalArgumentException("Arrangementet skal have en dato");
        }

        if (event.getDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Arrangementet skal ligge i fremtiden");
        }
    }
}
