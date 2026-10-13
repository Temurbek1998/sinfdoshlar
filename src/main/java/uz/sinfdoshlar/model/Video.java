package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** YouTube videosi (fayl saqlanmaydi, faqat video id'si). */
@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    @Column(nullable = false, length = 20)
    private String youtubeId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Video() {
    }

    public Video(User owner, String youtubeId, String title) {
        this.owner = owner;
        this.youtubeId = youtubeId;
        this.title = title;
    }

    public String getThumbnail() { return "https://img.youtube.com/vi/" + youtubeId + "/hqdefault.jpg"; }
    public String getEmbedUrl() { return "https://www.youtube-nocookie.com/embed/" + youtubeId + "?autoplay=1&rel=0"; }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public String getYoutubeId() { return youtubeId; }
    public String getTitle() { return title; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
