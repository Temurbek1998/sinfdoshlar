package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    /** Fayl ma'lumoti alohida jadvalda; bu yerda faqat uning id'si. */
    @Column(nullable = false)
    private Long mediaId;

    @Column(length = 500)
    private String caption;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Photo() {
    }

    public Photo(User owner, Long mediaId, String caption) {
        this.owner = owner;
        this.mediaId = mediaId;
        this.caption = caption;
    }

    public String getUrl() { return "/media/" + mediaId; }

    public Long getId() { return id; }
    public User getOwner() { return owner; }
    public Long getMediaId() { return mediaId; }
    public String getCaption() { return caption; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
