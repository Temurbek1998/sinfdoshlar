package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Notification;
import uz.sinfdoshlar.model.User;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop100ByRecipientOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndSeenFalse(User recipient);

    @Modifying
    @Query("update Notification n set n.seen = true where n.recipient = :u and n.seen = false")
    int markAllSeen(@Param("u") User user);
}
