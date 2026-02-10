package com.videostore.videostore.integration.rental;

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
public class RentalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        adminToken = registerAndLoginAdmin();

        userId = registerUser("user1", "user1@test.com", "Password12345");
        userToken = login("user1", "Password12345");
    }

    @Test
    void rentMovie_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie("Movie1", 1);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    @Test
    void rentMovie_shouldFailForUnauthenticatedUser() throws Exception {
        Long movieId = addMovie("Movie 1", 1);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rentMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(999L))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void rentMovie_shouldFailWhenMovieNotAvailable() throws Exception {
        Long movieId = addMovie("Movie 1", 1);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        registerUser("user2", "user2@test.com", "Password67890");
        String user2Token = login("user2", "Password67890");

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isConflict());
    }

    @Test
    void rentMovie_shouldFailWhenMovieAlreadyRentedByUser() throws Exception {
        Long movieId = addMovie("Movie 1", 2);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict());
    }

    @Test
    void returnMovie_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie("Movie1", 1);

        rentMovie(userToken, movieId);

        mockMvc.perform(delete("/rentals/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnMovie_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(delete("/rentals/{movieId}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnMovie_shouldFailWhenRentalDoesNotExist() throws Exception {
        mockMvc.perform(delete("/rentals/{movieId}", 999L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyRentals_shouldReturnListForAuthenticatedUser() throws Exception {
        Long movie1Id = addMovie("Movie 1", 1);
        rentMovie(userToken, movie1Id);

        Long movie2Id = addMovie("Movie 2", 1);
        rentMovie(userToken, movie2Id);

        mockMvc.perform(get("/me/rentals")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMyRentals_shouldReturnEmptyListWhenNoRentals() throws Exception {
        mockMvc.perform(get("/me/rentals")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getMyRentals_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/me/rentals"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getRentalsByUser_shouldReturnListForAdmin() throws Exception {
        Long movie1Id = addMovie("Movie 1", 1);
        rentMovie(userToken, movie1Id);

        Long movie2Id = addMovie("Movie 2", 1);
        rentMovie(userToken, movie2Id);

        mockMvc.perform(get("/users/{userId}/rentals", userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRentalsByUser_shouldReturnEmptyListWhenUserHasNoRentals() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRentalsByUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", userId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRentalsByUser_shouldFailWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRentalsByMovie_shouldReturnListForAdmin() throws Exception {
        Long movieId = addMovie("Movie 1", 2);

        registerUser("user2", "user2@test.com", "Password67890");
        String user2Token = login("user2", "Password67890");

        rentMovie(userToken, movieId);
        rentMovie(user2Token, movieId);

        mockMvc.perform(get("/movies/{movieId}/rentals", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRentalsByMovie_shouldReturnEmptyListWhenMovieHasNoRentals() throws Exception {
        Long movieId = addMovie("Movie 1", 2);

        mockMvc.perform(get("/movies/{movieId}/rentals", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRentalsByMovie_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/rentals", 1L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRentalsByMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/rentals", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
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

    private Long registerUser(String username, String email, String password) throws Exception {
        String body = """
                {
                  "name": "User",
                  "surname": "Example",
                  "username": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(username, email, password);

        String response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.id", Long.class);
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

    private Long addMovie(String title, int numberOfCopies) throws Exception {
        String body = """
                {
                  "title": "%s",
                  "year": 2000,
                  "genre": "Action",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis",
                  "numberOfCopies": %d
                }
                """.formatted(title, numberOfCopies);

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
        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    private String rentalBody(Long movieId) {
        return """
                {
                  "movieId": %d
                }
                """.formatted(movieId);
    }
}
