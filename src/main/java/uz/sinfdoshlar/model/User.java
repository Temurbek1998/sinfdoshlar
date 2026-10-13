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
