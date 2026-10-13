package uz.sinfdoshlar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.UploadException;
import uz.sinfdoshlar.service.UserService;
import uz.sinfdoshlar.service.VideoService;

@Controller
public class VideoController {

    private final UserService userService;
    private final VideoService videoService;

    public VideoController(UserService userService, VideoService videoService) {
        this.userService = userService;
        this.videoService = videoService;
    }

    @GetMapping("/video")
    public String list(@RequestParam(defaultValue = "all") String tab, Model model) {
        User me = userService.currentUser();
        boolean mine = "mine".equals(tab);
        model.addAttribute("tab", mine ? "mine" : "all");
        model.addAttribute("videos", mine ? videoService.of(me) : videoService.latest());
        return "videos";
    }

    @GetMapping("/video/{id}")
    public String watch(@PathVariable Long id, Model model) {
        model.addAttribute("video", videoService.get(id));
        model.addAttribute("more", videoService.latest().stream().filter(v -> !v.getId().equals(id)).limit(8).toList());
        return "video";
    }

    @PostMapping("/video")
    public String add(@RequestParam String url, @RequestParam(required = false) String title,
                      RedirectAttributes redirect) {
        try {
            var video = videoService.add(userService.currentUser(), url, title);
            return "redirect:/video/" + video.getId();
        } catch (UploadException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/video";
        }
    }

    @PostMapping("/video/{id}/delete")
    public String delete(@PathVariable Long id) {
        videoService.delete(userService.currentUser(), id);
        return "redirect:/video?tab=mine";
    }
}
