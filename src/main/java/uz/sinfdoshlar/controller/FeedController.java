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
import uz.sinfdoshlar.service.PostService;
import uz.sinfdoshlar.service.UserService;

@Controller
public class FeedController {

    private final UserService userService;
    private final PostService postService;
    private final FriendService friendService;

    public FeedController(UserService userService, PostService postService, FriendService friendService) {
        this.userService = userService;
        this.postService = postService;
        this.friendService = friendService;
    }

    @GetMapping("/")
    public String feed(Model model) {
        User me = userService.currentUser();
        model.addAttribute("posts", postService.feedFor(me));
        model.addAttribute("suggestions", userService.suggestionsFor(me, 5));
        model.addAttribute("myFriends", friendService.friendsOf(me));
        return "feed";
    }

    @PostMapping("/posts")
    public String create(@RequestParam String content, HttpServletRequest request) {
        postService.create(userService.currentUser(), content);
        return backTo(request);
    }

    @PostMapping("/posts/{id}/like")
    public String like(@PathVariable Long id, HttpServletRequest request) {
        postService.toggleLike(userService.currentUser(), id);
        return backTo(request);
    }

    @PostMapping("/posts/{id}/comments")
    public String comment(@PathVariable Long id, @RequestParam String content, HttpServletRequest request) {
        postService.comment(userService.currentUser(), id, content);
        return backTo(request) + "#post-" + id;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, HttpServletRequest request) {
        postService.deleteComment(userService.currentUser(), id);
        return backTo(request);
    }

    @PostMapping("/posts/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest request) {
        postService.delete(userService.currentUser(), id);
        return backTo(request);
    }

    /** Amal bajarilgandan keyin foydalanuvchi turgan sahifaga qaytaradi. */
    static String backTo(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            int start = referer.indexOf('/', referer.indexOf("//") + 2);
            if (start > 0 && !referer.startsWith("//", start)) {
                String path = referer.substring(start);
                int hash = path.indexOf('#');
                return "redirect:" + (hash >= 0 ? path.substring(0, hash) : path);
            }
        }
        return "redirect:/";
    }
}
