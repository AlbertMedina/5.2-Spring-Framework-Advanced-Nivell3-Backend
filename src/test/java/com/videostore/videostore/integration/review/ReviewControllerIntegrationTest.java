package com.videostore.videostore.integration.review;

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
public class ReviewControllerIntegrationTest {

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
        userToken = registerAndLoginUser("user1", "user1@test.com", "Password12345");
    }

    @Test
    void addReview_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addReview_shouldFailForUnauthenticatedUser() throws Exception {
        String body = reviewBody(1L, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addReview_shouldFailWhenMovieDoesNotExist() throws Exception {
        String body = reviewBody(1L, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addReview_shouldFailWhenMovieIsNotRentedByUser() throws Exception {
        Long movieId = addMovie();

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict());
    }

    @Test
    void addReview_shouldFailWhenMovieAlreadyReviewedByUser() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict());
    }

    @Test
    void addReview_shouldFailWithInvalidRating() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);

        String body = reviewBody(movieId, -1, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addReview_shouldFailWithInvalidComment() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);

        String body = reviewBody(movieId, 5, "");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeReview_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);

        addReview(userToken, movieId);

        mockMvc.perform(delete("/reviews/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeReview_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(delete("/reviews/{movieId}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeReview_shouldFailWhenReviewDoesNotExist() throws Exception {
        mockMvc.perform(delete("/reviews/{movieId}", 1L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviewsByMovie_shouldReturnList() throws Exception {
        Long movieId = addMovie();

        rentMovie(userToken, movieId);
        addReview(userToken, movieId);

        String user2Token = registerAndLoginUser("user2", "user2@test.com", "Password67890");
        rentMovie(user2Token, movieId);
        addReview(user2Token, movieId);

        mockMvc.perform(get("/movies/{movieId}/reviews", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReviewsByMovie_shouldReturnEmptyListWhenNoReviews() throws Exception {
        Long movieId = addMovie();

        mockMvc.perform(get("/movies/{movieId}/reviews", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getReviewsByMovie_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/reviews", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReviewsByMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/reviews", 1L)
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

    private String registerAndLoginUser(String username, String email, String password) throws Exception {
        String body = """
                {
                  "name": "User",
                  "surname": "Example",
                  "username": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(username, email, password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        return login(username, password);
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

    private Long addMovie() throws Exception {
        String body = """
                {
                  "title": "Movie1",
                  "year": 2000,
                  "genre": "Action",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis",
                  "numberOfCopies": 2
                }
                """;

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

    private void rentMovie(String userToken, Long movieId) throws Exception {
        String body = """
                {
                  "movieId": %d
                }
                """.formatted(movieId);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    private void addReview(String userToken, Long movieId) throws Exception {
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewBody(movieId, 5, "Comment"))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    private String reviewBody(Long movieId, int rating, String comment) {
        return """
                {
                  "movieId": %d,
                  "rating": %d,
                  "comment": "%s"
                }
                """.formatted(movieId, rating, comment);
    }
}
