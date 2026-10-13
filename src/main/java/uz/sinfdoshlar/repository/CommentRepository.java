package uz.sinfdoshlar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.sinfdoshlar.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
