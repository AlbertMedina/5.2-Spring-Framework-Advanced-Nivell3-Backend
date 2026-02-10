package com.videostore.videostore.integration.movie;

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
public class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    private Long userId;
    private Long adminId;

    @BeforeEach
    void setUp() throws Exception {
        userId = registerUser("user1", "user1@test.com", "Password12345");
        adminId = registerAdmin();

        userToken = login("user1", "Password12345");
        adminToken = login("admin", "Admin1234");
    }

    @Test
    void addMovie_shouldWorkForAdmin() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addMovie_shouldFailForNonAdmin() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void addMovie_shouldFailWithInvalidTitle() throws Exception {
        String body = """
                {
                  "title": "",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidYear() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "-1",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidGenre() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "_genre_",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidDuration() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "-1",
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidDirector() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "di/rector",
                  "synopsis": "synopsys",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidSynopsis() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "",
                  "numberOfCopies": "2"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMovie_shouldFailWithInvalidNumberOfCopies() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": "2000",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": "-1"
                }
                """;

        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovieInfo_shouldWorkForAdmin() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        String body = """
                {
                  "title": "new title",
                  "year": "2010",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis"
                }
                """;

        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.year").value(2010));
    }

    @Test
    void updateMovieInfo_shouldFailForNonAdmin() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        String body = """
                {
                  "title": "new title",
                  "year": "2010",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis"
                }
                """;

        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateMovieInfo_shouldFailWhenMovieDoesNotExist() throws Exception {
        addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        String body = """
                {
                  "title": "new title",
                  "year": "2010",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis"
                }
                """;

        mockMvc.perform(put("/movies/{movieId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovieInfo_shouldFailWithInvalidData() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        String body = """
                {
                  "title": "",
                  "year": "2010",
                  "genre": "genre",
                  "duration": "120",
                  "director": "director",
                  "synopsis": "synopsis"
                }
                """;

        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeMovie_shouldWorkForAdmin() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeMovie_shouldFailForNonAdmin() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        mockMvc.perform(delete("/movies/{movieId}", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeMovie_shouldFailWhenMovieHasActiveRentals() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        rentMovie(userToken, movieId);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void getMovie_shouldReturnMovie() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        mockMvc.perform(get("/movies/{movieId}", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    void getMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        Long movieId = addMovie("title", 2000, "genre", 120, "director", "synopsis", 2);

        mockMvc.perform(get("/movies/{movieId}", movieId))
                .andExpect(status().isNotFound());
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

    private Long registerAdmin() {
        User admin = User.create(
                null,
                new Name("Admin"),
                new Surname("Example"),
                new Username("admin"),
                new Email("admin@test.com"),
                new Password(passwordEncoder.encode("Admin1234")),
                Role.ADMIN
        );
        return userRepository.registerUser(admin).getId().value();
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

    private Long addMovie(String title, int year, String genre, int duration, String director, String synopsis, int numberOfCopies) throws Exception {
        String body = """
                {
                  "title": "%s",
                  "year": "%d",
                  "genre": "%s",
                  "duration": "%d",
                  "director": "%s",
                  "synopsis": "%s",
                  "numberOfCopies": "%d"
                }
                """.formatted(title, year, genre, duration, director, synopsis, numberOfCopies);

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
                  "movieId": "%d"
                }
                """.formatted(movieId);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }
}
