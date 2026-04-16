package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.application.UserService;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.model.User;
import dk.race.racekatteklubben.presentation.request.CreatePetRequest;
import dk.race.racekatteklubben.presentation.request.UpdatePetRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PetController {

    private final PetService petService;
    private final UserService userService;

    public PetController(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @GetMapping("/pets/create")
    public String showCreatePetPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("races", Race.values());
        return "create-cat";
    }

    @PostMapping("/pets/create")
    public String createPet(CreatePetRequest request,
                            HttpSession session,
                            Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            petService.createPetForOwner(request.name(), request.race(), user.getId());
            return "redirect:/profile";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("races", Race.values());
            model.addAttribute("name", request.name());
            model.addAttribute("selectedRace", request.race());
            model.addAttribute("error", ex.getMessage());
            return "create-cat";
        }
    }

    @GetMapping("/pets/edit-pet")
    public String showEditPetPage(@RequestParam(name = "id", required = false) Integer id,
                                  HttpSession session,
                                  Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        if (id == null) {
            return "redirect:/profile?missingPetId";
        }

        Pet pet;
        try {
            pet = petService.getPetById(id);
        } catch (IllegalArgumentException ex) {
            return "redirect:/profile?petNotFound";
        }

        if (pet.getOwnerId() != user.getId()) {
            return "redirect:/profile?notOwner";
        }

        model.addAttribute("pet", pet);
        model.addAttribute("races", Race.values());

        return "edit-pet"; // ✅ NOT redirect
    }

    @PostMapping("/pets/edit-pet")
    public String updatePet(UpdatePetRequest request,
                            HttpSession session,
                            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            petService.updatePetForOwner(request.id(), request.name(), request.race(), user.getId());
            return "redirect:/profile";

        } catch (IllegalArgumentException ex) {
            Pet pet = petService.getPetById(request.id());
            model.addAttribute("pet", pet);
            model.addAttribute("races", Race.values());
            model.addAttribute("error", ex.getMessage());
            return "edit-pet";
        }
    }

    @PostMapping("/pets/delete")
    public String deletePet(@RequestParam int id,
                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            petService.deletePetForOwner(id, user.getId());
            return "redirect:/profile?petDeleted";
        } catch (IllegalArgumentException ex) {
            return "redirect:/profile?petDeleteError";
        }
    }

    @GetMapping("/pets/all")
    public String showAllCats(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pets", petService.getAllPets());
        model.addAttribute("ownerUsernames", petService.getOwnerIdToUsernameMap(userService.findAllUsers()));
        return "cat-profil";
    }
}
