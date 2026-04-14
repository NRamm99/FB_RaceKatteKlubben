package dk.race.racekatteklubben.presentation.controller;

import dk.race.racekatteklubben.application.AuthService;
import dk.race.racekatteklubben.domain.model.Role;
import dk.race.racekatteklubben.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final AuthService authService;

    public HomeController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/index")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {
        try {
            User user = authService.login(username, password);
            session.setAttribute("loggedInUser", user);
            return "redirect:/profile";
        } catch (IllegalArgumentException ex) {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        try {
            User user = new User(
                    0,
                    username,
                    email,
                    "",
                    LocalDate.now(),
                    Role.USER,
                    List.of()
            );

            authService.register(user, password);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            return "redirect:/register?error";
        }
    }

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

}
