package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.exception.PetAlreadyAttendingException;
import dk.race.racekatteklubben.domain.exception.PetNotFoundException;
import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.repository.EventRepository;

import java.util.List;

public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event) {
        eventRepository.addEvent(event);
    }

    public void removeEvent(Event event) {
        eventRepository.removeEvent(event);
    }

    public List<Event> getEvents() {
        return eventRepository.getEvents();
    }

    public void addAttendingPet(int eventId, int petId) {
        eventRepository.addAttendingPet(eventId, petId);
    }

    public void removeAttendingPet(int eventId, int petId) {
        eventRepository.removeAttendingPet(eventId, petId);
    }

    public Event getEventById(int id) {
        Event event = eventRepository.getEventById(id);

        if (event != null) {
            List<Pet> pets = eventRepository.findAttendingPetsByEventId(id);
            event.setAttendingPets(pets);
        }

        return event;
    }
}
