package uz.sinfdoshlar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.AdminService;
import uz.sinfdoshlar.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Admin panel. Kirish SecurityConfig'da faqat ADMIN roli uchun ochiq. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService admin;
    private final UserService userService;

    public AdminController(AdminService admin, UserService userService) {
        this.admin = admin;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("section", "dashboard");
        model.addAttribute("stats", admin.stats());
        model.addAttribute("recent", admin.recentUsers());
        return "admin";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String q, Model model) {
        List<User> list = admin.users(q);
        Map<Long, Long> postCounts = new HashMap<>();
        list.forEach(u -> postCounts.put(u.getId(), admin.postCount(u)));
        model.addAttribute("section", "users");
        model.addAttribute("users", list);
        model.addAttribute("postCounts", postCounts);
        model.addAttribute("aq", q);
        return "admin";
    }

    @GetMapping("/posts")
    public String posts(Model model) {
        model.addAttribute("section", "posts");
        model.addAttribute("posts", admin.latestPosts());
        return "admin";
    }

    @GetMapping("/media")
    public String media(Model model) {
        model.addAttribute("section", "media");
        model.addAttribute("photos", admin.latestPhotos());
        model.addAttribute("tracks", admin.latestTracks());
        model.addAttribute("videos", admin.latestVideos());
        return "admin";
    }

    @PostMapping("/users/{id}/block")
    public String block(@PathVariable Long id, @RequestParam boolean value, RedirectAttributes ra) {
        ra.addFlashAttribute("success", admin.setBlocked(userService.currentUser(), id, value));
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/admin")
    public String makeAdmin(@PathVariable Long id, @RequestParam boolean value, RedirectAttributes ra) {
        ra.addFlashAttribute("success", admin.setAdmin(userService.currentUser(), id, value));
        return "redirect:/admin/users";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes ra) {
        admin.deletePost(id);
        ra.addFlashAttribute("success", "Post o'chirildi");
        return "redirect:/admin/posts";
    }

    @PostMapping("/photos/{id}/delete")
    public String deletePhoto(@PathVariable Long id, RedirectAttributes ra) {
        admin.deletePhoto(id);
        ra.addFlashAttribute("success", "Rasm o'chirildi");
        return "redirect:/admin/media";
    }

    @PostMapping("/tracks/{id}/delete")
    public String deleteTrack(@PathVariable Long id, RedirectAttributes ra) {
        admin.deleteTrack(id);
        ra.addFlashAttribute("success", "Qo'shiq o'chirildi");
        return "redirect:/admin/media";
    }

    @PostMapping("/videos/{id}/delete")
    public String deleteVideo(@PathVariable Long id, RedirectAttributes ra) {
        admin.deleteVideo(id);
        ra.addFlashAttribute("success", "Video o'chirildi");
        return "redirect:/admin/media";
    }
}
