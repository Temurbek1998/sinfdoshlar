package uz.sinfdoshlar.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.Comment;
import uz.sinfdoshlar.model.Friendship;
import uz.sinfdoshlar.model.FriendshipStatus;
import uz.sinfdoshlar.model.Message;
import uz.sinfdoshlar.model.Post;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.model.Video;
import uz.sinfdoshlar.repository.CommentRepository;
import uz.sinfdoshlar.repository.FriendshipRepository;
import uz.sinfdoshlar.repository.MessageRepository;
import uz.sinfdoshlar.repository.PostRepository;
import uz.sinfdoshlar.repository.UserRepository;
import uz.sinfdoshlar.repository.VideoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/** Baza bo'sh bo'lsa, sinab ko'rish uchun bir nechta foydalanuvchi yaratadi (parol: 123456). */
@Component
@org.springframework.core.annotation.Order(10)
@ConditionalOnProperty(name = "app.demo-data", havingValue = "true")
public class DemoDataLoader implements CommandLineRunner {

    private final UserRepository users;
    private final FriendshipRepository friendships;
    private final PostRepository posts;
    private final MessageRepository messages;
    private final CommentRepository comments;
    private final VideoRepository videos;
    private final PasswordEncoder encoder;

    public DemoDataLoader(UserRepository users, FriendshipRepository friendships, PostRepository posts,
                          MessageRepository messages, CommentRepository comments, VideoRepository videos,
                          PasswordEncoder encoder) {
        this.users = users;
        this.friendships = friendships;
        this.posts = posts;
        this.messages = messages;
        this.comments = comments;
        this.videos = videos;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() == 0) createUsers();
        if (videos.count() == 0) createVideos();
    }

    /** Video bo'limi bo'sh ko'rinmasligi uchun bir nechta ochiq YouTube videolari. */
    private void createVideos() {
        User owner = users.findByUsername("dilnoza").orElse(null);
        if (owner == null) return;
        videos.save(new Video(owner, "LXb3EKWsInQ", "Kosta-Rika tabiati 4K"));
        videos.save(new Video(owner, "aqz-KE-bpKQ", "Big Buck Bunny — multfilm"));
        users.findByUsername("jasur").ifPresent(j -> videos.save(new Video(j, "jNQXAC9IVRw", "YouTube'dagi birinchi video")));
    }

    private void createUsers() {

        User ali = user("ali", "Ali Valiyev", "Toshkent", LocalDate.of(1998, 3, 14));
        User dilnoza = user("dilnoza", "Dilnoza Karimova", "Samarqand", LocalDate.of(1999, 7, 2));
        User jasur = user("jasur", "Jasur Rahimov", "Buxoro", LocalDate.of(1997, 11, 21));
        User malika = user("malika", "Malika Usmonova", "Toshkent", LocalDate.of(2000, 1, 9));
        User sardor = user("sardor", "Sardor Tursunov", "Namangan", LocalDate.of(1996, 5, 30));
        User nodira = user("nodira", "Nodira Aliyeva", "Farg'ona", LocalDate.of(2001, 9, 17));
        user("kamola", "Kamola Ergasheva", "Andijon", LocalDate.of(1999, 12, 3));
        accepted(dilnoza, sardor);
        accepted(jasur, nodira);

        accepted(ali, dilnoza);
        accepted(ali, jasur);
        friendships.save(new Friendship(malika, ali)); // Ali uchun kutilayotgan so'rov

        Post p1 = new Post(dilnoza, "Hammaga salom! Sinfdoshlar bilan uchrashuv shanba kuni soat 18:00 da, "
                + "maktab yonidagi choyxonada bo'ladi 🎉\nKim keladi, izohda yozinglar!");
        p1.getLikedBy().add(jasur);
        p1.getLikedBy().add(sardor);
        posts.save(p1);
        Post p2 = posts.save(new Post(jasur, "Buxorodan salomlar! Kim bu yil maktabga borib keladi?"));
        p2.getLikedBy().add(dilnoza);
        posts.save(new Post(ali, "Yangi saytimizga xush kelibsiz! Do'stlaringizni taklif qiling 🙂"));

        comments.save(new Comment(p1, jasur, "Albatta kelaman!"));
        comments.save(new Comment(p1, sardor, "Men ham bo'laman, nimadir olib kelish kerakmi?"));

        messages.save(new Message(dilnoza, ali, "Salom Ali, uchrashuvga kelasanmi?"));
    }

    private User user(String username, String fullName, String city, LocalDate birthDate) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode("123456"));
        u.setFullName(fullName);
        u.setCity(city);
        u.setBirthDate(birthDate);
        u.setLastSeenAt(java.time.LocalDateTime.now().minusHours(3 + username.length() * 2L));
        return users.save(u);
    }

    private void accepted(User a, User b) {
        Friendship f = new Friendship(a, b);
        f.setStatus(FriendshipStatus.ACCEPTED);
        friendships.save(f);
    }
}
