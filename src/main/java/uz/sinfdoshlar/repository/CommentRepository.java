package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.sinfdoshlar.model.Comment;
import uz.sinfdoshlar.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** Foydalanuvchi muallif bo'lgan yoki izoh qoldirgan postlardagi izohlar, eng yangisi birinchi. */
    @Query("""
            select c from Comment c
            where c.post.author = :u
               or c.post.id in (select c2.post.id from Comment c2 where c2.author = :u)
            order by c.createdAt desc
            """)
    List<Comment> findInDiscussionsOf(@Param("u") User user);
}
