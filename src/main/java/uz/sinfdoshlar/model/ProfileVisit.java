package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Kim kimning sahifasiga kirgan ("Mehmonlar"). Har bir juftlik uchun bitta yozuv. */
@Entity
@Table(name = "profile_visits",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "visitor_id"}))
public class ProfileVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    @ManyToOne(optional = false)
    private User visitor;

    @Column(nullable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean seen = false;

    public ProfileVisit() {
    }

    public ProfileVisit(User owner, User visitor) {
        this.owner = owner;
        this.visitor = visitor;
    }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public User getVisitor() { return visitor; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
