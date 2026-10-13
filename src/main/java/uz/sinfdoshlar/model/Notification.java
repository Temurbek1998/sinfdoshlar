package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User recipient;

    @ManyToOne(optional = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    /** Like yoki izoh bo'lsa — qaysi post. */
    private Long postId;

    @Column(length = 200)
    private String preview;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {
    }

    public Notification(User recipient, User actor, NotificationType type, Long postId, String preview) {
        this.recipient = recipient;
        this.actor = actor;
        this.type = type;
        this.postId = postId;
        this.preview = preview;
    }

    public Long getId() { return id; }
    public User getRecipient() { return recipient; }
    public User getActor() { return actor; }
    public NotificationType getType() { return type; }
    public Long getPostId() { return postId; }
    public String getPreview() { return preview; }
    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
