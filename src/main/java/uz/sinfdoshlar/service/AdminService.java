package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Photo;
import uz.sinfdoshlar.model.Post;
import uz.sinfdoshlar.model.Track;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.model.Video;
import uz.sinfdoshlar.repository.CommentRepository;
import uz.sinfdoshlar.repository.MediaFileRepository;
import uz.sinfdoshlar.repository.MessageRepository;
import uz.sinfdoshlar.repository.PhotoRepository;
import uz.sinfdoshlar.repository.PostRepository;
import uz.sinfdoshlar.repository.TrackRepository;
import uz.sinfdoshlar.repository.UserRepository;
import uz.sinfdoshlar.repository.VideoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Admin panel: statistika va moderatsiya. */
@Service
public class AdminService {

    private final UserRepository users;
    private final PostRepository posts;
    private final CommentRepository comments;
    private final MessageRepository messages;
    private final PhotoRepository photos;
    private final TrackRepository tracks;
    private final VideoRepository videos;
    private final MediaFileRepository files;

    public AdminService(UserRepository users, PostRepository posts, CommentRepository comments,
                        MessageRepository messages, PhotoRepository photos, TrackRepository tracks,
                        VideoRepository videos, MediaFileRepository files) {
        this.users = users;
        this.posts = posts;
        this.comments = comments;
        this.messages = messages;
        this.photos = photos;
        this.tracks = tracks;
        this.videos = videos;
        this.files = files;
    }

    /** Bosh sahifadagi raqamlar (tartib saqlanadi). */
    @Transactional(readOnly = true)
    public Map<String, Object> stats() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("users", users.count());
        s.put("online", users.countByLastSeenAtAfter(LocalDateTime.now().minusMinutes(5)));
        s.put("newToday", users.countByCreatedAtAfter(today));
        s.put("blocked", users.countByBlockedTrue());
        s.put("posts", posts.count());
        s.put("postsToday", posts.countByCreatedAtAfter(today));
        s.put("comments", comments.count());
        s.put("messages", messages.count());
        s.put("photos", photos.count());
        s.put("tracks", tracks.count());
        s.put("videos", videos.count());
        s.put("storageMb", Math.round(files.totalSize() / 1048576.0 * 10) / 10.0);
        return s;
    }

    public List<User> recentUsers() {
        return users.findTop10ByOrderByCreatedAtDesc();
    }

    public List<User> users(String q) {
        return q == null || q.isBlank() ? users.findTop200ByOrderByCreatedAtDesc() : users.search(q.trim());
    }

    public long postCount(User u) {
        return posts.countByAuthor(u);
    }

    public List<Post> latestPosts() {
        return posts.findTop100ByOrderByCreatedAtDesc();
    }

    public List<Photo> latestPhotos() {
        return photos.findTop120ByOrderByCreatedAtDesc();
    }

    public List<Track> latestTracks() {
        return tracks.findTop100ByOrderByCreatedAtDesc();
    }

    public List<Video> latestVideos() {
        return videos.findTop60ByOrderByCreatedAtDesc();
    }

    @Transactional
    public String setBlocked(User admin, Long userId, boolean blocked) {
        User u = users.findById(userId).orElseThrow(NotFoundException::new);
        if (u.equals(admin)) return "O'zingizni bloklay olmaysiz";
        u.setBlocked(blocked);
        return u.getFullName() + (blocked ? " bloklandi" : " blokdan chiqarildi");
    }

    @Transactional
    public String setAdmin(User admin, Long userId, boolean value) {
        User u = users.findById(userId).orElseThrow(NotFoundException::new);
        if (u.equals(admin)) return "O'zingizning huquqingizni o'zgartira olmaysiz";
        u.setAdmin(value);
        return u.getFullName() + (value ? " admin qilindi (qayta kirgandan keyin ishlaydi)" : " endi admin emas");
    }

    @Transactional
    public void deletePost(Long id) {
        posts.findById(id).ifPresent(posts::delete);
    }

    @Transactional
    public void deletePhoto(Long id) {
        photos.findById(id).ifPresent(p -> {
            User owner = p.getOwner();
            if (p.getMediaId().equals(owner.getAvatarMediaId())) owner.setAvatarMediaId(null);
            photos.delete(p);
        });
    }

    @Transactional
    public void deleteTrack(Long id) {
        tracks.findById(id).ifPresent(t -> {
            tracks.unlinkFromUsers(t.getId());
            tracks.delete(t);
            files.deleteById(t.getMediaId());
        });
    }

    @Transactional
    public void deleteVideo(Long id) {
        videos.findById(id).ifPresent(videos::delete);
    }
}
