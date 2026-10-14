package uz.sinfdoshlar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.repository.UserRepository;

import java.util.Arrays;

/**
 * ADMINS muhit o'zgaruvchisidagi loginlarni (vergul bilan) ishga tushishda admin qiladi.
 * Masalan Render'da: ADMINS=bek
 */
@Component
@Order(100) // demo ma'lumotlardan keyin
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository users;
    private final String admins;

    public AdminBootstrap(UserRepository users, @Value("${app.admins:}") String admins) {
        this.users = users;
        this.admins = admins;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Arrays.stream(admins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(name -> users.findByUsername(name).ifPresent(u -> {
                    u.setAdmin(true);
                    u.setBlocked(false);
                    users.save(u);
                }));
    }
}
