package uz.sinfdoshlar.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.service.FriendService;
import uz.sinfdoshlar.service.MessageService;
import uz.sinfdoshlar.service.NotificationService;
import uz.sinfdoshlar.service.VisitService;
import uz.sinfdoshlar.service.UserService;

/** Har bir sahifaga yuqori menyu uchun kerakli ma'lumotlarni qo'shadi. */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UserService userService;
    private final FriendService friendService;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final VisitService visitService;

    public GlobalModelAdvice(UserService userService, FriendService friendService, MessageService messageService,
                             NotificationService notificationService, VisitService visitService) {
        this.userService = userService;
        this.friendService = friendService;
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.visitService = visitService;
    }

    @ModelAttribute
    public void addNavData(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
        User me = userService.currentUser();
        model.addAttribute("me", me);
        if (me != null) {
            model.addAttribute("unreadMessages", messageService.unreadCount(me));
            model.addAttribute("pendingRequests", friendService.incomingCount(me));
            model.addAttribute("unseenNotifications", notificationService.unseenCount(me));
            model.addAttribute("unseenGuests", visitService.unseenCount(me));
        }
    }
}
