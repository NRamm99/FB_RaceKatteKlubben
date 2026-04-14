package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProfileController {

    private final PetService petService;

    public ProfileController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        List<Pet> pets = petService.getPetsByOwnerId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("pets", pets);

        return "profile";
    }
}
