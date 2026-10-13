package uz.sinfdoshlar.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.sinfdoshlar.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;

/** Foydalanuvchining "oxirgi tashrif" vaqtini yangilaydi (bazaga ko'pi bilan daqiqada bir marta yoziladi). */
@Component
public class LastSeenFilter extends OncePerRequestFilter {

    private static final String SESSION_KEY = "lastSeenSavedAt";
    private final UserRepository users;

    public LastSeenFilter(UserRepository users) {
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        chain.doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return;
        HttpSession session = request.getSession(false);
        if (session == null) return;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime saved = (LocalDateTime) session.getAttribute(SESSION_KEY);
        if (saved == null || saved.isBefore(now.minusMinutes(1))) {
            users.touch(auth.getName(), now);
            session.setAttribute(SESSION_KEY, now);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/");
    }
}
