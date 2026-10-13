package uz.sinfdoshlar.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.FriendService;
import uz.sinfdoshlar.service.PhotoService;
import uz.sinfdoshlar.service.PostService;
import uz.sinfdoshlar.service.VideoService;
import uz.sinfdoshlar.service.VisitService;
import uz.sinfdoshlar.service.UserService;
import uz.sinfdoshlar.web.ProfileForm;

@Controller
public class ProfileController {

    private final UserService userService;
    private final FriendService friendService;
    private final PostService postService;
    private final PhotoService photoService;
    private final VideoService videoService;
    private final VisitService visitService;

    public ProfileController(UserService userService, FriendService friendService, PostService postService,
                             PhotoService photoService, VideoService videoService, VisitService visitService) {
        this.userService = userService;
        this.friendService = friendService;
        this.postService = postService;
        this.photoService = photoService;
        this.videoService = videoService;
        this.visitService = visitService;
    }

    @GetMapping("/profile")
    public String myProfile() {
        return "redirect:/profile/" + userService.currentUser().getUsername();
    }

    @GetMapping("/profile/{username}")
    public String profile(@PathVariable String username,
                          @RequestParam(defaultValue = "feed") String tab, Model model) {
        User me = userService.currentUser();
        User user = userService.byUsername(username);
        var posts = postService.byAuthor(user);
        visitService.record(user, me);
        model.addAttribute("photos", photoService.of(user));
        model.addAttribute("videos", videoService.of(user));
        model.addAttribute("user", user);
        model.addAttribute("relation", friendService.relation(me, user).name());
        model.addAttribute("friends", friendService.friendsOf(user));
        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", posts.size());
        model.addAttribute("likesReceived", postService.likesReceived(user));
        model.addAttribute("tab", java.util.Set.of("friends", "about", "photos", "videos").contains(tab) ? tab : "feed");
        // Profil sahifasida chap ustunda o'z rasmimiz ko'rinmaydi
        model.addAttribute("hideSidebarAvatar", true);
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
