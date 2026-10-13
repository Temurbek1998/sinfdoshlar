package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.ProfileVisit;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.ProfileVisitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitService {

    private final ProfileVisitRepository visits;

    public VisitService(ProfileVisitRepository visits) {
        this.visits = visits;
    }

    @Transactional
    public void record(User owner, User visitor) {
        if (owner.equals(visitor)) return;
        ProfileVisit visit = visits.findByOwnerAndVisitor(owner, visitor)
                .orElseGet(() -> new ProfileVisit(owner, visitor));
        visit.setVisitedAt(LocalDateTime.now());
        visit.setSeen(false);
        visits.save(visit);
    }

    public List<ProfileVisit> of(User owner) {
        return visits.findTop100ByOwnerOrderByVisitedAtDesc(owner);
    }

    public long unseenCount(User owner) {
        return visits.countByOwnerAndSeenFalse(owner);
    }

    @Transactional
    public void markAllSeen(User owner) {
        visits.markAllSeen(owner);
    }
}
