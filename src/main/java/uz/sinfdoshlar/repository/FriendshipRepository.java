package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Friendship;
import uz.sinfdoshlar.model.FriendshipStatus;
import uz.sinfdoshlar.model.User;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    @Query("""
            select f from Friendship f
            where (f.requester = :a and f.addressee = :b)
               or (f.requester = :b and f.addressee = :a)
            """)
    Optional<Friendship> findBetween(@Param("a") User a, @Param("b") User b);

    @Query("""
            select f from Friendship f
            where (f.requester = :u or f.addressee = :u) and f.status = :status
            """)
    List<Friendship> findAllOf(@Param("u") User user, @Param("status") FriendshipStatus status);

    List<Friendship> findByAddresseeAndStatusOrderByCreatedAtDesc(User addressee, FriendshipStatus status);

    List<Friendship> findByRequesterAndStatusOrderByCreatedAtDesc(User requester, FriendshipStatus status);

    long countByAddresseeAndStatus(User addressee, FriendshipStatus status);
}
