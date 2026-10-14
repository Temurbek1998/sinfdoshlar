package uz.sinfdoshlar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("update User u set u.lastSeenAt = :time where u.username = :username")
    void touch(@Param("username") String username, @Param("time") java.time.LocalDateTime time);

    boolean existsByUsername(String username);

    long countByCreatedAtAfter(java.time.LocalDateTime time);

    long countByLastSeenAtAfter(java.time.LocalDateTime time);

    long countByBlockedTrue();

    List<User> findTop10ByOrderByCreatedAtDesc();

    List<User> findTop200ByOrderByCreatedAtDesc();

    /** Do'st bo'lmagan va so'rov yuborilmagan foydalanuvchilar. */
    @Query("""
            select u from User u
            where u <> :me and not exists (
                select f from Friendship f
                where (f.requester = :me and f.addressee = u) or (f.requester = u and f.addressee = :me))
            order by u.createdAt desc
            """)
    List<User> suggestionsFor(@Param("me") User me, Pageable pageable);

    @Query("""
            select u from User u
            where lower(u.fullName) like lower(concat('%', :q, '%'))
               or lower(u.username) like lower(concat('%', :q, '%'))
               or lower(u.city) like lower(concat('%', :q, '%'))
            order by u.fullName
            """)
    List<User> search(@Param("q") String q);
}
