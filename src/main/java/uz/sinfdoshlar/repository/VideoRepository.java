package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.model.Video;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findTop60ByOrderByCreatedAtDesc();

    List<Video> findByOwnerOrderByCreatedAtDesc(User owner);
}
