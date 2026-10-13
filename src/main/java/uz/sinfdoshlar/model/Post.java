package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User author;

    @Column(nullable = false, length = 2000)
    private String content = "";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Postga biriktirilgan rasm (ixtiyoriy). */
    private Long imageMediaId;

    /** "Klass!" bosgan foydalanuvchilar. */
    @ManyToMany
    @JoinTable(name = "post_likes",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedBy = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("createdAt")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(User author, String content) {
        this.author = author;
        this.content = content;
    }

    public boolean isLikedBy(User user) {
        return likedBy.contains(user);
    }

    public Long getImageMediaId() { return imageMediaId; }
    public void setImageMediaId(Long imageMediaId) { this.imageMediaId = imageMediaId; }
    public String getImageUrl() { return imageMediaId == null ? null : "/media/" + imageMediaId; }

    public Long getId() { return id; }
    public User getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<User> getLikedBy() { return likedBy; }
    public List<Comment> getComments() { return comments; }
}
