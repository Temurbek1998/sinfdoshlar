package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.ProfileVisit;
import uz.sinfdoshlar.model.User;

import java.util.List;
import java.util.Optional;

public interface ProfileVisitRepository extends JpaRepository<ProfileVisit, Long> {

    Optional<ProfileVisit> findByOwnerAndVisitor(User owner, User visitor);

    List<ProfileVisit> findTop100ByOwnerOrderByVisitedAtDesc(User owner);

    long countByOwnerAndSeenFalse(User owner);

    @Modifying
    @Query("update ProfileVisit v set v.seen = true where v.owner = :u and v.seen = false")
    int markAllSeen(@Param("u") User user);
}
