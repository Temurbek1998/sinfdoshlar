package uz.sinfdoshlar.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.MusicService;
import uz.sinfdoshlar.service.UploadException;
import uz.sinfdoshlar.service.UserService;

@Controller
public class MusicController {

    private final UserService userService;
    private final MusicService musicService;

    public MusicController(UserService userService, MusicService musicService) {
        this.userService = userService;
        this.musicService = musicService;
    }

    @GetMapping("/music")
    public String music(@RequestParam(defaultValue = "popular") String tab,
                        @RequestParam(required = false) String q, Model model) {
        User me = userService.currentUser();
        boolean mine = "mine".equals(tab);
        model.addAttribute("tab", mine ? "mine" : "popular");
        model.addAttribute("mq", q);
        model.addAttribute("tracks", mine ? musicService.saved(me) : musicService.popular(q));
        model.addAttribute("savedIds", me.getSavedTracks().stream().map(t -> t.getId()).toList());
        return "music";
    }

    @PostMapping("/music")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false) String artist,
                         RedirectAttributes redirect) {
        try {
            musicService.upload(userService.currentUser(), file, title, artist);
            redirect.addFlashAttribute("success", "Qo'shiq yuklandi");
        } catch (UploadException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/music?tab=mine";
    }

    @PostMapping("/music/{id}/save")
    public String toggleSave(@PathVariable Long id, HttpServletRequest request) {
        musicService.toggleSaved(userService.currentUser(), id);
        return FeedController.backTo(request);
    }
}
