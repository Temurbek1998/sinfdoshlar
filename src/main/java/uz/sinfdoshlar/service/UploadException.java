package uz.sinfdoshlar.service;

/** Fayl yuklashda foydalanuvchiga ko'rsatiladigan xato. */
public class UploadException extends RuntimeException {
    public UploadException(String message) {
        super(message);
    }
}
