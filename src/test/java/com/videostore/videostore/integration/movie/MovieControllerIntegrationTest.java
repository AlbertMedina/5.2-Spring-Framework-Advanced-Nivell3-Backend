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

    @BeforeEach
    void setUp() throws Exception {
        adminToken = registerAndLoginAdmin();
    }

    @Test
    void addMovie_shouldWorkForAdmin() throws Exception {
        String body = """
                {
                  "title": "title",
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": 2
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
        String userToken = registerAndLoginUser();

        String body = """
                {
                  "title": "title",
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": 2
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
                  "year": -1,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "_genre_",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "genre",
                  "duration": -1,
                  "director": "director",
                  "synopsis": "synopsys",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "di/rector",
                  "synopsis": "synopsys",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "",
                  "numberOfCopies": 2
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
                  "year": 2000,
                  "genre": "genre",
                  "duration": 120,
                  "director": "director",
                  "synopsis": "synopsis",
                  "numberOfCopies": -1
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
        Long movieId = addMovie("Movie 1", "Action", 2);

        String body = """
                {
                  "title": "Movie 2",
                  "year": 2000,
                  "genre": "Drama",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis"
                }
                """;

        mockMvc.perform(put("/movies/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Movie 2"))
                .andExpect(jsonPath("$.genre").value("Drama"));
    }

    @Test
    void updateMovieInfo_shouldFailForNonAdmin() throws Exception {
        String userToken = registerAndLoginUser();

        Long movieId = addMovie("Movie 1", "Action", 2);

        String body = """
                {
                  "title": "Movie 2",
                  "year": 2000,
                  "genre": "Drama",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis"
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
        addMovie("Movie 1", "Action", 2);

        String body = """
                {
                  "title": "Movie 2",
                  "year": 2000,
                  "genre": "Drama",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis"
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
        Long movieId = addMovie("Movie 1", "Action", 2);

        String body = """
                {
                  "title": "",
                  "year": 2000,
                  "genre": "Drama",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis"
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
        Long movieId = addMovie("Movie 1", "Action", 2);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeMovie_shouldFailForNonAdmin() throws Exception {
        String userToken = registerAndLoginUser();

        Long movieId = addMovie("Movie 1", "Action", 2);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        addMovie("Movie 1", "Action", 2);

        mockMvc.perform(delete("/movies/{movieId}", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeMovie_shouldFailWhenMovieHasActiveRentals() throws Exception {
        String userToken = registerAndLoginUser();

        Long movieId = addMovie("Movie 1", "Action", 2);

        rentMovie(userToken, movieId);

        mockMvc.perform(delete("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void getMovie_shouldReturnMovie() throws Exception {
        String userToken = registerAndLoginUser();

        Long movieId = addMovie("Movie 1", "Action", 2);

        mockMvc.perform(get("/movies/{movieId}", movieId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Movie 1"));
    }

    @Test
    void getMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);

        mockMvc.perform(get("/movies/{movieId}", 999L)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllMovies_shouldReturnPagedList() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllMovies_shouldReturnEmptyListWhenNoMovies() throws Exception {
        String userToken = registerAndLoginUser();


        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllMovies_shouldFilterByGenre() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("genre", "action")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllMovies_shouldFilterByTitle() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("title", "1")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllMovies_shouldFilterOnlyAvailableMovies() throws Exception {
        String userToken = registerAndLoginUser();

        Long movie1Id = addMovie("Movie 1", "Action", 1);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        rentMovie(userToken, movie1Id);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("onlyAvailable", "true")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllMovies_shouldApplyMultipleFilters() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("genre", "action")
                        .param("title", "1")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllMovies_shouldSortAscending() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "TITLE")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[2].title").value("Movie 3"));
    }

    @Test
    void getAllMovies_shouldSortDescending() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "TITLE")
                        .param("ascending", "false")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Movie 3"))
                .andExpect(jsonPath("$[2].title").value("Movie 1"));
    }

    @Test
    void getAllMovies_shouldSortByRating() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);

        Long movie2Id = addMovie("Movie 2", "Action", 2);
        rentMovie(userToken, movie2Id);
        addReview(userToken, movie2Id, 5);

        Long movie3Id = addMovie("Movie 3", "Drama", 2);
        rentMovie(userToken, movie3Id);
        addReview(userToken, movie3Id, 4);

        mockMvc.perform(get("/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "RATING")
                        .param("ascending", "true")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("Movie 3"))
                .andExpect(jsonPath("$[2].title").value("Movie 1"));
    }

    @Test
    void getAllMovies_shouldFailWithInvalidPaginationParameters() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "-1")
                        .param("size", "10")
                        .param("sortBy", "TITLE")
                        .param("ascending", "false")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_shouldFailWithInvalidSortBy() throws Exception {
        String userToken = registerAndLoginUser();

        addMovie("Movie 1", "Action", 2);
        addMovie("Movie 2", "Action", 2);
        addMovie("Movie 3", "Drama", 2);

        mockMvc.perform(get("/movies")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "invalid")
                        .param("ascending", "false")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
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
                  "year": 2000,
                  "genre": "%s",
                  "duration": 120,
                  "director": "Director",
                  "synopsis": "Synopsis",
                  "numberOfCopies": %d
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

    private void addReview(String userToken, Long movieId, int rating) throws Exception {
        String body = """
                {
                  "movieId": %d,
                  "rating": %d,
                  "comment": "Comment"
                }
                """.formatted(movieId, rating);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }
}
