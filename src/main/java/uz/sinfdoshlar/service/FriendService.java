package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Friendship;
import uz.sinfdoshlar.model.FriendshipStatus;
import uz.sinfdoshlar.model.NotificationType;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.FriendshipRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class FriendService {

    /** Ikki foydalanuvchi o'rtasidagi munosabat (profil sahifasidagi tugmalar uchun). */
    public enum Relation { SELF, NONE, FRIENDS, REQUEST_SENT, REQUEST_RECEIVED }

    private final FriendshipRepository friendships;
    private final NotificationService notifications;

    public FriendService(FriendshipRepository friendships, NotificationService notifications) {
        this.friendships = friendships;
        this.notifications = notifications;
    }

    public List<User> friendsOf(User user) {
        return friendships.findAllOf(user, FriendshipStatus.ACCEPTED).stream()
                .map(f -> f.other(user))
                .sorted(Comparator.comparing(User::getFullName))
                .toList();
    }

    public List<Friendship> incomingRequests(User user) {
        return friendships.findByAddresseeAndStatusOrderByCreatedAtDesc(user, FriendshipStatus.PENDING);
    }

    public List<Friendship> outgoingRequests(User user) {
        return friendships.findByRequesterAndStatusOrderByCreatedAtDesc(user, FriendshipStatus.PENDING);
    }

    public long incomingCount(User user) {
        return friendships.countByAddresseeAndStatus(user, FriendshipStatus.PENDING);
    }

    public Relation relation(User me, User other) {
        if (me.equals(other)) return Relation.SELF;
        return friendships.findBetween(me, other)
                .map(f -> {
                    if (f.getStatus() == FriendshipStatus.ACCEPTED) return Relation.FRIENDS;
                    return f.getRequester().equals(me) ? Relation.REQUEST_SENT : Relation.REQUEST_RECEIVED;
                })
                .orElse(Relation.NONE);
    }

    @Transactional
    public void sendRequest(User me, User other) {
        if (me.equals(other)) return;
        var existing = friendships.findBetween(me, other);
        if (existing.isEmpty()) {
            friendships.save(new Friendship(me, other));
            notifications.notify(other, me, NotificationType.FRIEND_REQUEST, null, null);
        } else if (existing.get().getStatus() == FriendshipStatus.PENDING
                && existing.get().getAddressee().equals(me)) {
            // U menga allaqachon so'rov yuborgan bo'lsa, shunchaki qabul qilamiz.
            existing.get().setStatus(FriendshipStatus.ACCEPTED);
            notifications.notify(other, me, NotificationType.FRIEND_ACCEPTED, null, null);
        }
    }

    @Transactional
    public void accept(User me, User other) {
        friendships.findBetween(me, other)
                .filter(f -> f.getAddressee().equals(me) && f.getStatus() == FriendshipStatus.PENDING)
                .ifPresent(f -> {
                    f.setStatus(FriendshipStatus.ACCEPTED);
                    notifications.notify(other, me, NotificationType.FRIEND_ACCEPTED, null, null);
                });
    }

    /** So'rovni rad etish, bekor qilish yoki do'stlikdan chiqarish. */
    @Transactional
    public void remove(User me, User other) {
        friendships.findBetween(me, other).ifPresent(friendships::delete);
    }
}
