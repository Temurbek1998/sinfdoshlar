package uz.sinfdoshlar.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.UserRepository;
import uz.sinfdoshlar.web.ProfileForm;
import uz.sinfdoshlar.web.RegisterForm;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public boolean usernameTaken(String username) {
        return users.existsByUsername(username);
    }

    @Transactional
    public User register(RegisterForm form) {
        User user = new User();
        user.setUsername(form.getUsername().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFullName(form.getFullName().trim());
        user.setCity(blankToNull(form.getCity()));
        user.setBirthDate(form.getBirthDate());
        return users.save(user);
    }

    @Transactional
    public void updateProfile(User user, ProfileForm form) {
        user.setFullName(form.getFullName().trim());
        user.setCity(blankToNull(form.getCity()));
        user.setBirthDate(form.getBirthDate());
        user.setAbout(blankToNull(form.getAbout()));
        users.save(user);
    }

    /** Hozir tizimga kirgan foydalanuvchi yoki null. */
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return users.findByUsername(auth.getName()).orElse(null);
    }

    public User byUsername(String username) {
        return users.findByUsername(username).orElseThrow(NotFoundException::new);
    }

    public List<User> search(String q) {
        return q == null || q.isBlank() ? List.of() : users.search(q.trim());
    }

    public List<User> suggestionsFor(User me, int limit) {
        return users.suggestionsFor(me, PageRequest.of(0, limit));
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
