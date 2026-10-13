package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.model.Video;
import uz.sinfdoshlar.repository.VideoRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VideoService {

    /** youtube.com/watch?v=ID, youtu.be/ID, youtube.com/shorts/ID, youtube.com/embed/ID */
    private static final Pattern YOUTUBE = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|shorts/|embed/|live/)|youtu\\.be/)([A-Za-z0-9_-]{11})");

    private final VideoRepository videos;

    public VideoService(VideoRepository videos) {
        this.videos = videos;
    }

    public List<Video> latest() {
        return videos.findTop60ByOrderByCreatedAtDesc();
    }

    public List<Video> of(User owner) {
        return videos.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public Video get(Long id) {
        return videos.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Video add(User owner, String url, String title) {
        String id = youtubeId(url);
        if (id == null) throw new UploadException("YouTube havolasini to'g'ri kiriting");
        String name = title == null || title.isBlank() ? "Video" : title.strip();
        if (name.length() > 200) name = name.substring(0, 200);
        return videos.save(new Video(owner, id, name));
    }

    @Transactional
    public void delete(User owner, Long id) {
        videos.findById(id).filter(v -> v.getOwner().equals(owner)).ifPresent(videos::delete);
    }

    static String youtubeId(String url) {
        if (url == null) return null;
        Matcher m = YOUTUBE.matcher(url.trim());
        return m.find() ? m.group(1) : null;
    }
}
