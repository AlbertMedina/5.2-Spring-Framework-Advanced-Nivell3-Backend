package com.videostore.videostore.integration.favourite;

import com.jayway.jsonpath.JsonPath;
import com.videostore.videostore.TestContainersConfiguration;
import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfiguration.class)
public class FavouriteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = registerAndLoginAdmin();
        userToken = registerAndLoginUser();
    }

    @Test
    void addFavourite_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie("Movie 1", "Action", 1);

        String body = favouriteBody(movieId);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addFavourite_shouldFailWhenMovieDoesNotExist() throws Exception {
        String body = favouriteBody(1L);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFavourite_shouldFailWhenFavouriteAlreadyExists() throws Exception {
        Long movieId = addMovie("Movie 1", "Action", 1);

        String body = favouriteBody(movieId);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict());
    }

    @Test
    void removeFavourite_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie("Movie 1", "Action", 1);
        addFavourite(movieId);

        mockMvc.perform(delete("/favourites/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeFavourite_shouldFailWhenFavouriteDoesNotExist() throws Exception {
        mockMvc.perform(delete("/favourites/{movieId}", 1L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyFavourites_shouldReturnListForAuthenticatedUser() throws Exception {
        Long movie1Id = addMovie("Movie 1", "Action", 1);
        addFavourite(movie1Id);

        Long movie2Id = addMovie("Movie 2", "Drama", 1);
        addFavourite(movie2Id);

        mockMvc.perform(get("/me/favourites")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMyFavourites_shouldReturnEmptyListWhenNoFavourites() throws Exception {
        mockMvc.perform(get("/me/favourites")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private String registerAndLoginAdmin() throws Exception {
        User admin = User.create(
                null,
                new Name("Admin"),
                new Surname("Example"),
                new Username("admin"),
                new Email("admin@test.com"),
                new Password(passwordEncoder.encode("Admin1234")),
                Role.ADMIN
        );
        userRepository.registerUser(admin);

        return login("admin", "Admin1234");
    }

    private String registerAndLoginUser() throws Exception {
        String body = """
                {
                  "name": "User",
                  "surname": "Example",
                  "username": "user1",
                  "email": "user1@test.com",
                  "password": "Password12345"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        return login("user1", "Password12345");
    }

    private String login(String login, String password) throws Exception {
        String body = """
                {
                  "loginIdentifier": "%s",
                  "password": "%s"
                }
                """.formatted(login, password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.token", String.class);
    }

    private Long addMovie(String title, String genre, int numberOfCopies) throws Exception {
        String body = """
                {
                  "title": "%s",
                  "year": "2000",
                  "genre": "%s",
                  "duration": "120",
                  "director": "Director",
                  "synopsis": "Synopsis",
                  "numberOfCopies": "%d"
                }
                """.formatted(title, genre, numberOfCopies);

        String response = mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.id", Long.class);
    }

    private void addFavourite(Long movieId) throws Exception {
        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favouriteBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    private String favouriteBody(Long movieId) {
        return """
                {
                  "movieId": %d
                }
                """.formatted(movieId);
    }
}
