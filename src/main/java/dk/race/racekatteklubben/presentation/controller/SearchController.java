package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.application.UserService;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    private final UserService userService;
    private final PetService petService;

    public SearchController(UserService userService, PetService petService) {
        this.userService = userService;
        this.petService = petService;
    }

    @GetMapping("/search")
    public String showSearchPage(@RequestParam(name = "q", required = false) String query,
                                 HttpSession session,
                                 Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<User> users = userService.searchUsers(query);
        List<Pet> pets = petService.searchPets(query);
        Map<Integer, String> ownerUsernames = petService.getOwnerIdToUsernameMap(userService.findAllUsers());

        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("users", users);
        model.addAttribute("pets", pets);
        model.addAttribute("ownerUsernames", ownerUsernames);

        return "search";
    }

    @GetMapping("/users/{id}")
    public String showUserProfile(@PathVariable int id,
                                  HttpSession session,
                                  Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findUserById(id);
            List<Pet> pets = petService.getPetsByOwnerId(user.getId());

            model.addAttribute("user", user);
            model.addAttribute("pets", pets);
            return "user-profile";
        } catch (IllegalArgumentException ex) {
            return "redirect:/search?userNotFound";
        }
    }

    @GetMapping("/pets/{id}")
    public String showPetProfile(@PathVariable int id,
                                 HttpSession session,
                                 Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        try {
            Pet pet = petService.getPetById(id);
            User owner = userService.findUserById(pet.getOwnerId());

            model.addAttribute("pet", pet);
            model.addAttribute("owner", owner);
            return "pet-profile";
        } catch (IllegalArgumentException ex) {
            return "redirect:/search?petNotFound";
        }
    }
}
