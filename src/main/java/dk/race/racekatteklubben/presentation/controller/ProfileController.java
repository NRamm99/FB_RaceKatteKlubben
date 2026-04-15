package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.EventService;
import dk.race.racekatteklubben.application.PetService;
import dk.race.racekatteklubben.application.UserService;
import dk.race.racekatteklubben.domain.model.Event;
import dk.race.racekatteklubben.domain.model.Pet;
import dk.race.racekatteklubben.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    private final PetService petService;
    private final EventService eventService;
    private final UserService userService;

    public ProfileController(PetService petService, EventService eventService, UserService userService) {
        this.petService = petService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        List<Pet> pets = petService.getPetsByOwnerId(user.getId());
        List<Event> upcomingEvents = eventService.getUpcomingEventsByOwnerId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("pets", pets);
        model.addAttribute("upcomingEvents", upcomingEvents);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditUserPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/profile/edit")
    public String editUser(@RequestParam String username,
                           @RequestParam String email,
                           HttpSession session,
                           Model model) {
        User loggedinUser = (User) session.getAttribute("loggedInUser");

        if (loggedinUser == null) {
            return "redirect:/login";
        }

        try {
            User updateUser = userService.updateProfile(loggedinUser, username, email);
            session.setAttribute("loggedInUser", updateUser);
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            User formUser = new User(
                    loggedinUser.getId(),
                    username,
                    email,
                    loggedinUser.getPasswordHash(),
                    loggedinUser.getSignUpDate(),
                    loggedinUser.getRole(),
                    loggedinUser.getPets()
            );

            model.addAttribute("user", formUser);
            model.addAttribute("error", e.getMessage());
            return "edit-user";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordPage(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        return "change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String repeatPassword,
                                 HttpSession session,
                                 Model model) {
        User loggedinUser = (User) session.getAttribute("loggedInUser");

        if (loggedinUser == null) {
            return "redirect:/login";
        }

        try {
            userService.changePassword(loggedinUser, oldPassword, newPassword, repeatPassword);
            session.setAttribute("loggedInUser", loggedinUser);
            return "redirect:/profile";
        }catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "change-password";
        }
    }
}
