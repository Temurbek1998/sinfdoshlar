package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.sinfdoshlar.model.Photo;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.PhotoRepository;
import uz.sinfdoshlar.repository.UserRepository;

import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photos;
    private final UserRepository users;
    private final MediaService mediaService;

    public PhotoService(PhotoRepository photos, UserRepository users, MediaService mediaService) {
        this.photos = photos;
        this.users = users;
        this.mediaService = mediaService;
    }

    public List<Photo> of(User owner) {
        return photos.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public long count(User owner) {
        return photos.countByOwner(owner);
    }

    @Transactional
    public Photo upload(User owner, MultipartFile file, String caption, boolean makeAvatar) {
        var media = mediaService.storeImage(file);
        String text = caption == null || caption.isBlank() ? null : caption.strip();
        if (text != null && text.length() > 500) text = text.substring(0, 500);
        Photo photo = photos.save(new Photo(owner, media.getId(), text));
        if (makeAvatar) {
            owner.setAvatarMediaId(media.getId());
            users.save(owner);
        }
        return photo;
    }

    @Transactional
    public void makeAvatar(User owner, Long photoId) {
        photos.findById(photoId)
                .filter(p -> p.getOwner().equals(owner))
                .ifPresent(p -> {
                    owner.setAvatarMediaId(p.getMediaId());
                    users.save(owner);
                });
    }

    @Transactional
    public void removeAvatar(User owner) {
        owner.setAvatarMediaId(null);
        users.save(owner);
    }

    @Transactional
    public void delete(User owner, Long photoId) {
        photos.findById(photoId)
                .filter(p -> p.getOwner().equals(owner))
                .ifPresent(p -> {
                    if (p.getMediaId().equals(owner.getAvatarMediaId())) {
                        owner.setAvatarMediaId(null);
                        users.save(owner);
                    }
                    photos.delete(p);
                    // Rasm postga biriktirilgan bo'lsa, fayl qoladi — post rasmisiz qolmasin
                });
    }
}
