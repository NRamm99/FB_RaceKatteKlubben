package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.EventService;
import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.presentation.request.CreateEventRequest;
import dk.race.racekatteklubben.presentation.request.UpdateEventRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class EventsController {

    private final EventService eventService;
    private final PetService petService;

    public EventsController(EventService eventService, PetService petService) {
        this.eventService = eventService;
        this.petService = petService;
    }

    @GetMapping("/events")
    public String showEventsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("events", eventService.getEvents());
        model.addAttribute("currentUserId", user.getId());
        return "events";
    }

    @GetMapping("/events/{eventId}/participate")
    public String showParticipationPage(@PathVariable int eventId,
                                        HttpSession session,
                                        Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Event event = eventService.getEventById(eventId);
        if (event == null) {
            return "redirect:/events?error";
        }

        List<Pet> pets = petService.getPetsByOwnerId(user.getId());
        Set<Integer> attendingPetIds = event.getAttendingPets()
                .stream()
                .map(Pet::getId)
                .collect(Collectors.toSet());

        model.addAttribute("event", event);
        model.addAttribute("pets", pets);
        model.addAttribute("attendingPetIds", attendingPetIds);

        return "participate-event";
    }

    @GetMapping("/events/create")
    public String showCreateEventPage(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        return "create-event";
    }

    @GetMapping("/events/{eventId}/edit")
    public String showEditEventPage(@PathVariable int eventId,
                                    HttpSession session,
                                    Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Event event = eventService.getEventById(eventId);
        if (event == null || event.getOwnerId() != user.getId()) {
            return "redirect:/events?error";
        }

        model.addAttribute("event", event);
        return "edit-event";
    }

    @PostMapping("/events/create")
    public String createEvent(CreateEventRequest request,
                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            eventService.createEventForOwner(user.getId(), request.title(), request.description(), request.dateTime());
            return "redirect:/events?created";
        } catch (IllegalArgumentException ex) {
            return "redirect:/events/create?error";
        }
    }

    @PostMapping("/events/{eventId}/edit")
    public String updateEvent(@PathVariable int eventId,
                              UpdateEventRequest request,
                              HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            eventService.updateEventForOwner(eventId, user.getId(), request.title(), request.description(), request.dateTime());
            return "redirect:/events?updated";
        } catch (IllegalArgumentException ex) {
            return "redirect:/events/" + eventId + "/edit?error";
        }
    }

    @PostMapping("/events/{eventId}/participate")
    public String updateParticipation(@PathVariable int eventId,
                                      @RequestParam(name = "petIds", required = false) List<Integer> petIds,
                                      HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Event event = eventService.getEventById(eventId);
        if (event == null) {
            return "redirect:/events?error";
        }

        List<Pet> ownerPets = petService.getPetsByOwnerId(user.getId());
        Map<Integer, Pet> ownerPetsById = new HashMap<>();
        for (Pet pet : ownerPets) {
            ownerPetsById.put(pet.getId(), pet);
        }

        Set<Integer> selectedPetIds = petIds == null ? new HashSet<>() : new HashSet<>(petIds);
        if (!ownerPetsById.keySet().containsAll(selectedPetIds)) {
            return "redirect:/events/" + eventId + "/participate?error";
        }

        Set<Integer> currentOwnerAttendingPetIds = event.getAttendingPets()
                .stream()
                .map(Pet::getId)
                .filter(ownerPetsById::containsKey)
                .collect(Collectors.toSet());

        for (Integer petId : selectedPetIds) {
            if (!currentOwnerAttendingPetIds.contains(petId)) {
                eventService.addAttendingPet(eventId, petId);
            }
        }

        for (Integer petId : currentOwnerAttendingPetIds) {
            if (!selectedPetIds.contains(petId)) {
                eventService.removeAttendingPet(eventId, petId);
            }
        }

        return "redirect:/events?joined";
    }

    @PostMapping("/events/{eventId}/delete")
    public String deleteEvent(@PathVariable int eventId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            eventService.deleteEventForOwner(eventId, user.getId());
            return "redirect:/events?deleted";
        } catch (IllegalArgumentException ex) {
            return "redirect:/events?error";
        }
    }
}
