package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Message;
import uz.sinfdoshlar.model.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
            select m from Message m
            where (m.sender = :a and m.recipient = :b)
               or (m.sender = :b and m.recipient = :a)
            order by m.createdAt
            """)
    List<Message> findConversation(@Param("a") User a, @Param("b") User b);

    @Query("select m from Message m where m.sender = :u or m.recipient = :u order by m.createdAt desc")
    List<Message> findAllOf(@Param("u") User user);

    long countByRecipientAndSeenFalse(User recipient);

    @Modifying
    @Query("update Message m set m.seen = true where m.sender = :from and m.recipient = :to and m.seen = false")
    int markSeen(@Param("from") User from, @Param("to") User to);
}
