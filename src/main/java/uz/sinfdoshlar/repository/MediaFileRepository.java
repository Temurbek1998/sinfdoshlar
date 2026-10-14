package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sinfdoshlar.model.MediaFile;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    @org.springframework.data.jpa.repository.Query("select coalesce(sum(m.size), 0) from MediaFile m")
    long totalSize();
}
