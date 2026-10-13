package uz.sinfdoshlar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.DiscussionService;
import uz.sinfdoshlar.service.NotificationService;
import uz.sinfdoshlar.service.UserService;
import uz.sinfdoshlar.service.VisitService;

/** Bildirishnomalar, mehmonlar, muhokamalar va telefon uchun menyu. */
@Controller
public class ActivityController {

    private final UserService userService;
    private final NotificationService notificationService;
    private final VisitService visitService;
    private final DiscussionService discussionService;

    public ActivityController(UserService userService, NotificationService notificationService,
                              VisitService visitService, DiscussionService discussionService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.visitService = visitService;
        this.discussionService = discussionService;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        User me = userService.currentUser();
        model.addAttribute("items", notificationService.of(me));
        notificationService.markAllSeen(me);
        model.addAttribute("unseenNotifications", 0L);
        return "notifications";
    }

    @GetMapping("/guests")
    public String guests(Model model) {
        User me = userService.currentUser();
        model.addAttribute("visits", visitService.of(me));
        visitService.markAllSeen(me);
        model.addAttribute("unseenGuests", 0L);
        return "guests";
    }

    @GetMapping("/discussions")
    public String discussions(Model model) {
        model.addAttribute("posts", discussionService.of(userService.currentUser()));
        return "discussions";
    }

    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }
}
