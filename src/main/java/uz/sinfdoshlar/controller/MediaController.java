package uz.sinfdoshlar.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uz.sinfdoshlar.model.MediaFile;
import uz.sinfdoshlar.service.MediaService;

import java.util.concurrent.TimeUnit;

/** Rasm va qo'shiqlarni beradi. Resource qaytarilgani uchun Range so'rovlari (audio aylantirish) ishlaydi. */
@RestController
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<Resource> get(@PathVariable Long id) {
        MediaFile file = mediaService.get(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePrivate().immutable())
                .body(new ByteArrayResource(file.getData()));
    }
}
