package uz.sinfdoshlar.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.FriendService;
import uz.sinfdoshlar.service.UserService;

@Controller
public class FriendController {

    private final UserService userService;
    private final FriendService friendService;

    public FriendController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    @GetMapping("/friends")
    public String friends(Model model) {
        User me = userService.currentUser();
        model.addAttribute("friends", friendService.friendsOf(me));
        model.addAttribute("incoming", friendService.incomingRequests(me));
        model.addAttribute("outgoing", friendService.outgoingRequests(me));
        return "friends";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("q", q);
        model.addAttribute("results", userService.search(q));
        return "search";
    }

    @PostMapping("/friends/{username}/add")
    public String add(@PathVariable String username, HttpServletRequest request) {
        friendService.sendRequest(userService.currentUser(), userService.byUsername(username));
        return FeedController.backTo(request);
    }

    @PostMapping("/friends/{username}/accept")
    public String accept(@PathVariable String username, HttpServletRequest request) {
        friendService.accept(userService.currentUser(), userService.byUsername(username));
        return FeedController.backTo(request);
    }

    @PostMapping("/friends/{username}/remove")
    public String remove(@PathVariable String username, HttpServletRequest request) {
        friendService.remove(userService.currentUser(), userService.byUsername(username));
        return FeedController.backTo(request);
    }
}
