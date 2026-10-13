package uz.sinfdoshlar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.MessageService;
import uz.sinfdoshlar.service.UserService;

@Controller
public class MessageController {

    private final UserService userService;
    private final MessageService messageService;

    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    public String inbox(Model model) {
        model.addAttribute("conversations", messageService.conversationsOf(userService.currentUser()));
        return "messages";
    }

    @GetMapping("/messages/{username}")
    public String conversation(@PathVariable String username, Model model) {
        User me = userService.currentUser();
        User partner = userService.byUsername(username);
        if (me.equals(partner)) {
            return "redirect:/messages";
        }
        model.addAttribute("partner", partner);
        model.addAttribute("messages", messageService.openConversation(me, partner));
        // Xabarlar o'qildi — menyudagi hisoblagichni yangilaymiz
        model.addAttribute("unreadMessages", messageService.unreadCount(me));
        return "conversation";
    }

    @PostMapping("/messages/{username}")
    public String send(@PathVariable String username, @RequestParam String content) {
        messageService.send(userService.currentUser(), userService.byUsername(username), content);
        return "redirect:/messages/" + username;
    }
}
