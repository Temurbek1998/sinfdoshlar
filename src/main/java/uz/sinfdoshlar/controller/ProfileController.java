package uz.sinfdoshlar.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.FriendService;
import uz.sinfdoshlar.service.PostService;
import uz.sinfdoshlar.service.UserService;
import uz.sinfdoshlar.web.ProfileForm;

@Controller
public class ProfileController {

    private final UserService userService;
    private final FriendService friendService;
    private final PostService postService;

    public ProfileController(UserService userService, FriendService friendService, PostService postService) {
        this.userService = userService;
        this.friendService = friendService;
        this.postService = postService;
    }

    @GetMapping("/profile")
    public String myProfile() {
        return "redirect:/profile/" + userService.currentUser().getUsername();
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username, Model model) {
        User me = userService.currentUser();
        User user = userService.byUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("relation", friendService.relation(me, user).name());
        model.addAttribute("friends", friendService.friendsOf(user));
        model.addAttribute("posts", postService.byAuthor(user));
        return "profile";
    }

    @GetMapping("/settings")
    public String editForm(Model model) {
        model.addAttribute("form", ProfileForm.of(userService.currentUser()));
        return "settings";
    }

    @PostMapping("/settings")
    public String save(@Valid @ModelAttribute("form") ProfileForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "settings";
        }
        User me = userService.currentUser();
        userService.updateProfile(me, form);
        return "redirect:/profile/" + me.getUsername();
    }
}
