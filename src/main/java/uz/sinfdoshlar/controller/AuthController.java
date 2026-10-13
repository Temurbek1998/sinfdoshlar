package uz.sinfdoshlar.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.sinfdoshlar.service.UserService;
import uz.sinfdoshlar.web.RegisterForm;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return userService.currentUser() != null ? "redirect:/" : "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult result,
                           RedirectAttributes redirect) {
        if (form.getUsername() != null && userService.usernameTaken(form.getUsername().trim().toLowerCase())) {
            result.rejectValue("username", "taken", "Bu login band");
        }
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Parollar mos kelmadi");
        }
        if (result.hasErrors()) {
            return "register";
        }
        userService.register(form);
        redirect.addFlashAttribute("success", "Ro'yxatdan o'tdingiz! Endi tizimga kiring.");
        return "redirect:/login";
    }
}
