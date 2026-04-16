package dk.race.racekatteklubben.application;

import dk.race.racekatteklubben.domain.exception.PetAlreadyAttendingException;
import dk.race.racekatteklubben.domain.exception.PetNotFoundException;
import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.repository.EventRepository;
import dk.race.racekatteklubben.domain.repository.PetRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    @Test
    void addEventSavesEventInRepository() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(1, "Cat Show");

        assertDoesNotThrow(() -> eventService.addEvent(event));

        assertEquals(1, repository.savedEvents.size());
        assertEquals(event, repository.savedEvents.get(0));
    }

    @Test
    void removeEventDeletesEventInRepository() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(1, "Cat Show");
        repository.savedEvents.add(event);

        assertDoesNotThrow(() -> eventService.removeEvent(event));

        assertEquals(1, repository.deletedEvents.size());
        assertEquals(event, repository.deletedEvents.get(0));
    }

    @Test
    void getEventsReturnsAllEvents() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);

        Event firstEvent = createEvent(1, "Cat Show");
        Event secondEvent = createEvent(2, "Vet Visit");

        repository.savedEvents.add(firstEvent);
        repository.savedEvents.add(secondEvent);

        List<Event> events = eventService.getEvents();

        assertEquals(2, events.size());
        assertEquals(firstEvent, events.get(0));
        assertEquals(secondEvent, events.get(1));
    }

    @Test
    void getEventByIdReturnsEventWithAttendingPets() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);

        Event event = createEvent(1, "Cat Show");
        Pet firstPet = createPet(1, "Milo");
        Pet secondPet = createPet(2, "Luna");

        repository.savedEvents.add(event);
        repository.attendingPetsToReturn.add(firstPet);
        repository.attendingPetsToReturn.add(secondPet);

        Event foundEvent = eventService.getEventById(1);

        assertNotNull(foundEvent);
        assertEquals(event.getId(), foundEvent.getId());
        assertEquals("Cat Show", foundEvent.getTitle());
        assertEquals(2, foundEvent.getAttendingPets().size());
        assertEquals(firstPet, foundEvent.getAttendingPets().get(0));
        assertEquals(secondPet, foundEvent.getAttendingPets().get(1));
    }

    @Test
    void getEventByIdReturnsNullWhenEventDoesNotExist() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);

        Event foundEvent = eventService.getEventById(99);

        assertNull(foundEvent);
    }

    @Test
    void getUpcomingEventsByOwnerIdReturnsRepositoryResult() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(4, "Race Day");
        repository.upcomingEventsToReturn.add(event);

        List<Event> upcomingEvents = eventService.getUpcomingEventsByOwnerId(1);

        assertEquals(1, upcomingEvents.size());
        assertEquals(event, upcomingEvents.get(0));
        assertEquals(1, repository.lastUpcomingEventsOwnerId);
        assertNotNull(repository.lastUpcomingEventsFromDateTime);
    }

    @Test
    void addAttendingPetCallsRepository() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(3, "Cat Show");
        Pet pet = createPet(7, "Luna");
        repository.savedEvents.add(event);
        petRepository.save(pet);

        assertDoesNotThrow(() -> eventService.addAttendingPet(3, 7));

        assertEquals(1, repository.addedPetEventLinks.size());
        PetEventLink link = repository.addedPetEventLinks.get(0);

        assertEquals(3, link.eventId());
        assertEquals(7, link.petId());
    }

    @Test
    void removeAttendingPetCallsRepository() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(3, "Cat Show");
        Pet pet = createPet(7, "Luna");
        repository.savedEvents.add(event);
        repository.attendingPetsToReturn.add(pet);
        petRepository.save(pet);

        assertDoesNotThrow(() -> eventService.removeAttendingPet(3, 7));

        assertEquals(1, repository.removedPetEventLinks.size());
        PetEventLink link = repository.removedPetEventLinks.get(0);

        assertEquals(3, link.eventId());
        assertEquals(7, link.petId());
    }

    @Test
    void addAttendingPetThrowsWhenPetIsAlreadyAttending() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(3, "Cat Show");
        Pet pet = createPet(7, "Luna");
        repository.savedEvents.add(event);
        repository.attendingPetsToReturn.add(pet);
        petRepository.save(pet);

        PetAlreadyAttendingException exception = assertThrows(
                PetAlreadyAttendingException.class,
                () -> eventService.addAttendingPet(3, 7)
        );

        assertEquals("Katten er allerede tilmeldt", exception.getMessage());
    }

    @Test
    void removeAttendingPetThrowsWhenPetIsNotAttending() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(3, "Cat Show");
        Pet pet = createPet(7, "Luna");
        repository.savedEvents.add(event);
        petRepository.save(pet);

        PetNotFoundException exception = assertThrows(
                PetNotFoundException.class,
                () -> eventService.removeAttendingPet(3, 7)
        );

        assertEquals("Katten er ikke tilmeldt begivenheden", exception.getMessage());
    }

    @Test
    void addAttendingPetThrowsWhenPetDoesNotExist() {
        InMemoryEventRepository repository = new InMemoryEventRepository();
        InMemoryPetRepository petRepository = new InMemoryPetRepository();
        EventService eventService = new EventService(repository, petRepository);
        Event event = createEvent(3, "Cat Show");
        repository.savedEvents.add(event);

        PetNotFoundException exception = assertThrows(
                PetNotFoundException.class,
                () -> eventService.addAttendingPet(3, 99)
        );

        assertEquals("Katten blev ikke fundet", exception.getMessage());
    }

    private static Event createEvent(int id, String title) {
        Event event = new Event(
                id,
                1,
                title,
                "A lovely cat event",
                LocalDateTime.of(2026, 5, 20, 14, 30)
        );
        event.replaceAttendingPets(new ArrayList<>());
        return event;
    }

    private static Pet createPet(int id, String name) {
        return new Pet(id, name, Race.MAINE_COON, 1);
    }

    private static final class InMemoryEventRepository implements EventRepository {
        private final List<Event> savedEvents = new ArrayList<>();
        private final List<Event> deletedEvents = new ArrayList<>();
        private final List<Pet> attendingPetsToReturn = new ArrayList<>();
        private final List<PetEventLink> addedPetEventLinks = new ArrayList<>();
        private final List<PetEventLink> removedPetEventLinks = new ArrayList<>();
        private final List<Event> upcomingEventsToReturn = new ArrayList<>();
        private Integer lastUpcomingEventsOwnerId;
        private LocalDateTime lastUpcomingEventsFromDateTime;

        @Override
        public void addEvent(Event event) {
            savedEvents.add(event);
        }

        @Override
        public void updateEvent(Event event) {

        }

        @Override
        public void removeEvent(Event event) {
            deletedEvents.add(event);
        }

        @Override
        public List<Event> getEvents() {
            return List.copyOf(savedEvents);
        }

        @Override
        public List<Event> getUpcomingEventsByOwnerId(int ownerId, LocalDateTime fromDateTime) {
            lastUpcomingEventsOwnerId = ownerId;
            lastUpcomingEventsFromDateTime = fromDateTime;
            return List.copyOf(upcomingEventsToReturn);
        }

        @Override
        public Event getEventById(int id) {
            return savedEvents.stream()
                    .filter(event -> event.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void addAttendingPet(int eventId, int petId) {
            addedPetEventLinks.add(new PetEventLink(eventId, petId));
        }

        @Override
        public void removeAttendingPet(int eventId, int petId) {
            removedPetEventLinks.add(new PetEventLink(eventId, petId));
        }

        @Override
        public List<Pet> findAttendingPetsByEventId(int eventId) {
            return List.copyOf(attendingPetsToReturn);
        }
    }

    private static final class InMemoryPetRepository implements PetRepository {
        private final List<Pet> pets = new ArrayList<>();

        @Override
        public void save(Pet pet) {
            pets.add(pet);
        }

        @Override
        public void update(Pet pet) {
        }

        @Override
        public void deleteById(int id) {
        }

        @Override
        public Pet findById(int id) {
            return pets.stream()
                    .filter(pet -> pet.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Pet> findAll() {
            return List.copyOf(pets);
        }

        @Override
        public List<Pet> findByOwnerId(int ownerId) {
            return pets.stream()
                    .filter(pet -> pet.getOwnerId() == ownerId)
                    .toList();
        }
    }

    private record PetEventLink(int eventId, int petId) {
    }
}
