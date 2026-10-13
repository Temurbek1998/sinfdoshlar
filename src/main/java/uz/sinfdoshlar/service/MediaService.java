package uz.sinfdoshlar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.sinfdoshlar.model.MediaFile;
import uz.sinfdoshlar.repository.MediaFileRepository;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** Rasm va audio fayllarni bazaga saqlaydi. Rasmlar kichraytirilib JPEG qilinadi. */
@Service
public class MediaService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;
    private static final long MAX_AUDIO_BYTES = 10L * 1024 * 1024;
    private static final int MAX_IMAGE_SIDE = 1280;

    private final MediaFileRepository files;

    public MediaService(MediaFileRepository files) {
        this.files = files;
    }

    @Transactional
    public MediaFile storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new UploadException("Rasm tanlanmagan");
        if (file.getSize() > MAX_IMAGE_BYTES) throw new UploadException("Rasm 5 MB dan katta bo'lmasin");
        BufferedImage src;
        try {
            src = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            src = null;
        }
        if (src == null) throw new UploadException("Faqat JPG, PNG yoki GIF rasm yuklash mumkin");
        return files.save(new MediaFile("image/jpeg", toJpeg(src)));
    }

    @Transactional
    public MediaFile storeAudio(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new UploadException("Fayl tanlanmagan");
        if (file.getSize() > MAX_AUDIO_BYTES) throw new UploadException("Qo'shiq 10 MB dan katta bo'lmasin");
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        String type = file.getContentType() == null ? "" : file.getContentType();
        boolean mp3 = name.endsWith(".mp3") || type.equals("audio/mpeg") || type.equals("audio/mp3");
        if (!mp3) throw new UploadException("Faqat MP3 fayl yuklash mumkin");
        try {
            return files.save(new MediaFile("audio/mpeg", file.getBytes()));
        } catch (IOException e) {
            throw new UploadException("Faylni o'qib bo'lmadi");
        }
    }

    public MediaFile get(Long id) {
        return files.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) files.deleteById(id);
    }

    private static byte[] toJpeg(BufferedImage src) {
        double scale = Math.min(1.0, (double) MAX_IMAGE_SIDE / Math.max(src.getWidth(), src.getHeight()));
        int w = Math.max(1, (int) Math.round(src.getWidth() * scale));
        int h = Math.max(1, (int) Math.round(src.getHeight() * scale));
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE); // shaffof PNG uchun oq fon
        g.fillRect(0, 0, w, h);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(bytes)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.85f);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(out, null, null), param);
            writer.dispose();
            ios.flush();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new UploadException("Rasmni saqlab bo'lmadi");
        }
    }
}
