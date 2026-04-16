package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.exception.PetNotFoundException;
import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.repository.EventRepository;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final PetRepository petRepository;

    public EventService(EventRepository eventRepository, PetRepository petRepository) {
        this.eventRepository = eventRepository;
        this.petRepository = petRepository;
    }

    public void addEvent(Event event) {
        validateEvent(event);
        eventRepository.addEvent(event);
    }

    public void createEventForOwner(int ownerId, String title, String description, LocalDateTime dateTime) {
        addEvent(new Event(0, ownerId, title, description, dateTime));
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

    public void updateEventForOwner(int eventId, int ownerId, String title, String description, LocalDateTime dateTime) {
        updateEvent(new Event(eventId, ownerId, title, description, dateTime));
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

    public void deleteEventForOwner(int eventId, int ownerId) {
        Event existingEvent = eventRepository.getEventById(eventId);
        if (existingEvent == null) {
            throw new IllegalArgumentException("Begivenheden blev ikke fundet");
        }

        if (existingEvent.getOwnerId() != ownerId) {
            throw new IllegalArgumentException("Du ejer ikke denne begivenhed");
        }

        eventRepository.removeEvent(existingEvent);
    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }

    public List<Event> getUpcomingEventsByOwnerId(int ownerId) {
        if (ownerId <= 0) {
            throw new IllegalArgumentException("Ugyldigt ejer-id");
        }

        return eventRepository.getUpcomingEventsByOwnerId(ownerId, LocalDateTime.now());
    }

    public void addAttendingPet(int eventId, int petId) {
        Event event = requireEventWithAttendingPets(eventId);
        Pet pet = requirePet(petId);

        event.addAttendingPet(pet);
        eventRepository.addAttendingPet(eventId, petId);
    }

    public void removeAttendingPet(int eventId, int petId) {
        Event event = requireEventWithAttendingPets(eventId);
        Pet pet = requirePet(petId);

        event.removeAttendingPet(pet);
        eventRepository.removeAttendingPet(eventId, petId);
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

    private Event requireEventWithAttendingPets(int eventId) {
        Event event = eventRepository.getEventById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Begivenheden blev ikke fundet");
        }

        event.replaceAttendingPets(eventRepository.findAttendingPetsByEventId(eventId));
        return event;
    }

    private Pet requirePet(int petId) {
        Pet pet = petRepository.findById(petId);
        if (pet == null) {
            throw new PetNotFoundException("Katten blev ikke fundet");
        }

        return pet;
    }
}
