package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Post;
import uz.sinfdoshlar.model.User;

import java.util.Collection;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.author in :authors order by p.createdAt desc")
    List<Post> findFeed(@Param("authors") Collection<User> authors);

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
}
