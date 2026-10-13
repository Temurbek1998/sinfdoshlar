package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Notification;
import uz.sinfdoshlar.model.NotificationType;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional
    public void notify(User recipient, User actor, NotificationType type, Long postId, String preview) {
        if (recipient.equals(actor)) return;
        String text = preview == null ? null : (preview.length() > 120 ? preview.substring(0, 117) + "..." : preview);
        notifications.save(new Notification(recipient, actor, type, postId, text));
    }

    public List<Notification> of(User user) {
        return notifications.findTop100ByRecipientOrderByCreatedAtDesc(user);
    }

    public long unseenCount(User user) {
        return notifications.countByRecipientAndSeenFalse(user);
    }

    @Transactional
    public void markAllSeen(User user) {
        notifications.markAllSeen(user);
    }
}
