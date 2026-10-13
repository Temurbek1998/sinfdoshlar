package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.sinfdoshlar.model.Track;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.TrackRepository;
import uz.sinfdoshlar.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicService {

    private final TrackRepository tracks;
    private final UserRepository users;
    private final MediaService mediaService;

    public MusicService(TrackRepository tracks, UserRepository users, MediaService mediaService) {
        this.tracks = tracks;
        this.users = users;
        this.mediaService = mediaService;
    }

    public List<Track> popular(String q) {
        return q == null || q.isBlank() ? tracks.findTop100ByOrderByCreatedAtDesc() : tracks.search(q.trim());
    }

    public List<Track> saved(User user) {
        return new ArrayList<>(user.getSavedTracks());
    }

    @Transactional
    public Track upload(User owner, MultipartFile file, String title, String artist) {
        var media = mediaService.storeAudio(file);
        String name = title;
        if (name == null || name.isBlank()) {
            String original = file.getOriginalFilename() == null ? "Nomsiz" : file.getOriginalFilename();
            name = original.replaceAll("(?i)\\.mp3$", "");
        }
        Track track = tracks.save(new Track(owner, media.getId(), cut(name.strip(), 200),
                artist == null || artist.isBlank() ? null : cut(artist.strip(), 200)));
        owner.getSavedTracks().add(track);
        users.save(owner);
        return track;
    }

    @Transactional
    public void toggleSaved(User user, Long trackId) {
        Track track = tracks.findById(trackId).orElseThrow(NotFoundException::new);
        if (!user.getSavedTracks().remove(track)) {
            user.getSavedTracks().add(track);
        }
        users.save(user);
    }

    private static String cut(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }
}
