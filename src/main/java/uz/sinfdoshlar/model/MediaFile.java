package uz.sinfdoshlar.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Yuklangan fayl (rasm yoki qo'shiq) — baza ichida saqlanadi. */
@Entity
@Table(name = "media_files")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = 20_000_000)
    private byte[] data;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MediaFile() {
    }

    public MediaFile(String contentType, byte[] data) {
        this.contentType = contentType;
        this.data = data;
        this.size = data.length;
    }

    public Long getId() { return id; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public byte[] getData() { return data; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
