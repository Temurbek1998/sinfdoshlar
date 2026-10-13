package uz.sinfdoshlar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uz.sinfdoshlar.model.User;
import uz.sinfdoshlar.repository.UserRepository;
import uz.sinfdoshlar.service.FriendService;
import uz.sinfdoshlar.service.FriendService.Relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SinfdoshlarApplicationTests {

    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired FriendService friendService;

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void registerThenOpenFeed() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("fullName", "Test Testov")
                        .param("username", "testuser")
                        .param("password", "secret1")
                        .param("confirmPassword", "secret1"))
                .andExpect(redirectedUrl("/login"));
        assertThat(users.findByUsername("testuser")).isPresent();
    }

    @Test
    void registerRejectsMismatchedPasswords() throws Exception {
        mvc.perform(post("/register").with(csrf())
                        .param("fullName", "X")
                        .param("username", "mismatch")
                        .param("password", "secret1")
                        .param("confirmPassword", "other12"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        assertThat(users.findByUsername("mismatch")).isEmpty();
    }

    @Test
    void friendRequestFlow() {
        User a = save("fa"), b = save("fb");
        friendService.sendRequest(a, b);
        assertThat(friendService.relation(a, b)).isEqualTo(Relation.REQUEST_SENT);
        assertThat(friendService.relation(b, a)).isEqualTo(Relation.REQUEST_RECEIVED);
        friendService.accept(b, a);
        assertThat(friendService.relation(a, b)).isEqualTo(Relation.FRIENDS);
        assertThat(friendService.friendsOf(a)).containsExactly(b);
        friendService.remove(a, b);
        assertThat(friendService.relation(a, b)).isEqualTo(Relation.NONE);
    }

    @Test
    @WithMockUser(username = "feeduser")
    void loggedInUserSeesFeed() throws Exception {
        save("feeduser");
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("feed"));
    }

    private User save(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("x");
        u.setFullName(username.toUpperCase());
        return users.save(u);
    }
}
