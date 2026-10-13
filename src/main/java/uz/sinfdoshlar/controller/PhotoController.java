package uz.sinfdoshlar.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.PhotoService;
import uz.sinfdoshlar.service.UploadException;
import uz.sinfdoshlar.service.UserService;

@Controller
public class PhotoController {

    private final UserService userService;
    private final PhotoService photoService;

    public PhotoController(UserService userService, PhotoService photoService) {
        this.userService = userService;
        this.photoService = photoService;
    }

    @GetMapping("/photos")
    public String myPhotos(Model model) {
        User me = userService.currentUser();
        model.addAttribute("photos", photoService.of(me));
        return "photos";
    }

    @PostMapping("/photos")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(required = false) String caption,
                         @RequestParam(defaultValue = "false") boolean avatar,
                         HttpServletRequest request, RedirectAttributes redirect) {
        try {
            photoService.upload(userService.currentUser(), file, caption, avatar);
            redirect.addFlashAttribute("success", avatar ? "Profil rasmi yangilandi" : "Rasm yuklandi");
        } catch (UploadException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return FeedController.backTo(request);
    }

    @PostMapping("/photos/{id}/avatar")
    public String makeAvatar(@PathVariable Long id, HttpServletRequest request) {
        photoService.makeAvatar(userService.currentUser(), id);
        return FeedController.backTo(request);
    }

    @PostMapping("/photos/avatar/remove")
    public String removeAvatar(HttpServletRequest request) {
        photoService.removeAvatar(userService.currentUser());
        return FeedController.backTo(request);
    }

    @PostMapping("/photos/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest request) {
        photoService.delete(userService.currentUser(), id);
        return FeedController.backTo(request);
    }
}
