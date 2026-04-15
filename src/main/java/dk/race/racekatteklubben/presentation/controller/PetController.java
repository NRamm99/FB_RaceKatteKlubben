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
}