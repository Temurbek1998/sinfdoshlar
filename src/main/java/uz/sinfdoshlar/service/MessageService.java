package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Message;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.MessageRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    /** Suhbatlar ro'yxatidagi bitta qator. */
    public record Conversation(User partner, Message lastMessage, long unread) {
    }

    private final MessageRepository messages;

    public MessageService(MessageRepository messages) {
        this.messages = messages;
    }

    public List<Conversation> conversationsOf(User me) {
        Map<User, Message> last = new LinkedHashMap<>();
        Map<User, Long> unread = new LinkedHashMap<>();
        for (Message m : messages.findAllOf(me)) {
            User partner = m.getSender().equals(me) ? m.getRecipient() : m.getSender();
            last.putIfAbsent(partner, m);
            if (m.getRecipient().equals(me) && !m.isSeen()) {
                unread.merge(partner, 1L, Long::sum);
            }
        }
        List<Conversation> result = new ArrayList<>();
        last.forEach((partner, msg) -> result.add(new Conversation(partner, msg, unread.getOrDefault(partner, 0L))));
        return result;
    }

    /** Suhbatni ochadi va undagi kiruvchi xabarlarni o'qilgan deb belgilaydi. */
    @Transactional
    public List<Message> openConversation(User me, User partner) {
        messages.markSeen(partner, me);
        return messages.findConversation(me, partner);
    }

    @Transactional
    public void send(User from, User to, String content) {
        if (content == null || content.isBlank() || from.equals(to)) return;
        String text = content.strip();
        if (text.length() > 2000) text = text.substring(0, 2000);
        messages.save(new Message(from, to, text));
    }

    public long unreadCount(User me) {
        return messages.countByRecipientAndSeenFalse(me);
    }
}
