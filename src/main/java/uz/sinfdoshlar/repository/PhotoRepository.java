package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sinfdoshlar.model.Photo;
import uz.sinfdoshlar.model.User;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByOwnerOrderByCreatedAtDesc(User owner);

    long countByOwner(User owner);

    List<Photo> findTop120ByOrderByCreatedAtDesc();
}
