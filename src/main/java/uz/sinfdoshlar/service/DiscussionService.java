package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import uz.sinfdoshlar.model.Comment;
import uz.sinfdoshlar.model.Post;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.CommentRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** "Muhokamalar": men yozgan yoki izoh qoldirgan postlar, oxirgi izoh bo'yicha tartiblangan. */
@Service
public class DiscussionService {

    private final CommentRepository comments;

    public DiscussionService(CommentRepository comments) {
        this.comments = comments;
    }

    public List<Post> of(User user) {
        Set<Post> posts = new LinkedHashSet<>();
        for (Comment c : comments.findInDiscussionsOf(user)) {
            posts.add(c.getPost());
            if (posts.size() >= 30) break;
        }
        return new ArrayList<>(posts);
    }
}
