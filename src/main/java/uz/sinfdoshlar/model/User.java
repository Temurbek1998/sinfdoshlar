package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 100)
    private String city;

    private LocalDate birthDate;

    @Column(length = 1000)
    private String about;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastSeenAt;

    /** Profil rasmi (media_files jadvalidagi id) — bo'lmasa rangli doira. */
    private Long avatarMediaId;

    /** "Mening musiqam" ro'yxati. */
    @ManyToMany
    @JoinTable(name = "user_tracks",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id"))
    @OrderBy("createdAt desc")
    private java.util.Set<Track> savedTracks = new java.util.LinkedHashSet<>();

    /** Avatar doirasida ko'rsatiladigan bosh harf. */
    public String getInitial() {
        return fullName == null || fullName.isBlank() ? "?" : fullName.substring(0, 1).toUpperCase();
    }

    /** Avatar foni: har bir foydalanuvchiga o'z rangi. */
    public String getAvatarColor() {
        int[] hues = {28, 200, 145, 265, 340, 175, 45, 225};
        int hue = hues[(int) ((id == null ? 0 : id) % hues.length)];
        return "hsl(" + hue + ", 65%, 55%)";
    }

    public String getAvatarUrl() {
        return avatarMediaId == null ? null : "/media/" + avatarMediaId;
    }

    /** Avatar elementining style atributi: rasm bo'lsa — rasm, bo'lmasa — rang. */
    public String getAvatarStyle() {
        return avatarMediaId == null
                ? "background:" + getAvatarColor()
                : "background:" + getAvatarColor() + " url(/media/" + avatarMediaId + ") center/cover no-repeat";
    }

    /** Rasm bo'lsa harf ko'rsatilmaydi. */
    public String getAvatarLetter() {
        return avatarMediaId == null ? getInitial() : "";
    }

    public Long getAvatarMediaId() { return avatarMediaId; }
    public void setAvatarMediaId(Long avatarMediaId) { this.avatarMediaId = avatarMediaId; }
    public java.util.Set<Track> getSavedTracks() { return savedTracks; }

    public Integer getAge() {
        return birthDate == null ? null : java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    /** So'nggi 5 daqiqada faol bo'lgan bo'lsa — saytda. */
    public boolean isOnline() {
        return lastSeenAt != null && lastSeenAt.isAfter(LocalDateTime.now().minusMinutes(5));
    }

    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
