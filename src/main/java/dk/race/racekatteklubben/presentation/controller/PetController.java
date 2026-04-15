package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.Race;
import dk.race.racekatteklubben.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
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
    public String createPet(@RequestParam String name,
                            @RequestParam Race race,
                            HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            Pet pet = new Pet(
                    0,
                    name,
                    race,
                    user.getId()
            );

            petService.createPet(pet);
            return "redirect:/profile";
        } catch (IllegalArgumentException ex) {
            return "redirect:/pets/create?error";
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
    public String updatePet(@RequestParam int id,
                            @RequestParam String name,
                            @RequestParam Race race,
                            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            Pet pet = petService.getPetById(id);

            if (pet.getOwnerId() != user.getId()) {
                return "redirect:/profile";
            }

            pet.changeName(name);
            pet.changeRace(race);

            petService.updatePet(pet);

            return "redirect:/profile";

        } catch (IllegalArgumentException ex) {
            return "redirect:/pets/edit-pet?id=" + id + "&error";
        }
    }
}
