package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracks")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    @Column(nullable = false)
    private Long mediaId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String artist;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Track() {
    }

    public Track(User owner, Long mediaId, String title, String artist) {
        this.owner = owner;
        this.mediaId = mediaId;
        this.title = title;
        this.artist = artist;
    }

    public String getUrl() { return "/media/" + mediaId; }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public Long getMediaId() { return mediaId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
