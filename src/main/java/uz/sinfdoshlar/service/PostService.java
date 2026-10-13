package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Comment;
import uz.sinfdoshlar.model.Post;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.CommentRepository;
import uz.sinfdoshlar.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository posts;
    private final CommentRepository comments;
    private final FriendService friendService;

    public PostService(PostRepository posts, CommentRepository comments, FriendService friendService) {
        this.posts = posts;
        this.comments = comments;
        this.friendService = friendService;
    }

    /** Lenta: o'zimning va do'stlarimning postlari, eng yangisi tepada. */
    public List<Post> feedFor(User user) {
        List<User> authors = new ArrayList<>(friendService.friendsOf(user));
        authors.add(user);
        return posts.findFeed(authors);
    }

    public List<Post> byAuthor(User author) {
        return posts.findByAuthorOrderByCreatedAtDesc(author);
    }

    @Transactional
    public void create(User author, String content) {
        if (content == null || content.isBlank()) return;
        String text = content.strip();
        if (text.length() > 2000) text = text.substring(0, 2000);
        posts.save(new Post(author, text));
    }

    @Transactional
    public void toggleLike(User user, Long postId) {
        Post post = posts.findById(postId).orElseThrow(NotFoundException::new);
        if (!post.getLikedBy().remove(user)) {
            post.getLikedBy().add(user);
        }
    }

    @Transactional
    public void comment(User user, Long postId, String content) {
        if (content == null || content.isBlank()) return;
        Post post = posts.findById(postId).orElseThrow(NotFoundException::new);
        String text = content.strip();
        if (text.length() > 1000) text = text.substring(0, 1000);
        comments.save(new Comment(post, user, text));
    }

    @Transactional
    public void deleteComment(User user, Long commentId) {
        comments.findById(commentId)
                .filter(c -> c.getAuthor().equals(user) || c.getPost().getAuthor().equals(user))
                .ifPresent(comments::delete);
    }

    @Transactional
    public void delete(User user, Long postId) {
        posts.findById(postId)
                .filter(p -> p.getAuthor().equals(user))
                .ifPresent(posts::delete);
    }
}
