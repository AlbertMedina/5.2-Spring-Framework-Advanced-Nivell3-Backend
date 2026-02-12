package com.videostore.videostore.integration;

import com.jayway.jsonpath.JsonPath;
import com.videostore.videostore.TestContainersConfiguration;
import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.domain.repository.UserRepository;
import com.videostore.videostore.infrastructure.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfiguration.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected AuthenticatedTestUser registerAndLogin(String name, String surname, String username, String email, String password, boolean isAdmin) throws Exception {
        Long id = isAdmin ?
                registerAdmin(name, surname, username, email, password) :
                registerUser(name, surname, username, email, password);
        String token = login(username, password);

        return new AuthenticatedTestUser(id, token);
    }

    private Long registerAdmin(String name, String surname, String username, String email, String password) {
        User admin = User.create(
                null,
                new Name(name),
                new Surname(surname),
                new Username(username),
                new Email(email),
                new Password(passwordEncoder.encode(password)),
                Role.ADMIN
        );
        return userRepository.registerUser(admin).getId().value();
    }

    private Long registerUser(String name, String surname, String username, String email, String password) throws Exception {
        String body = registerBody(name, surname, username, email, password);

        String response = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.id", Long.class);
    }

    protected String registerBody(String name, String surname, String username, String email, String password) {
        return """
                {
                  "name": "%s",
                  "surname": "%s",
                  "username": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(name, surname, username, email, password);
    }

    protected String login(String loginIdentifier, String password) throws Exception {
        String body = loginBody(loginIdentifier, password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.token", String.class);
    }

    protected String loginBody(String loginIdentifier, String password) {
        return """
                {
                      "loginIdentifier": "%s",
                      "password": "%s"
                    }
                """.formatted(loginIdentifier, password);
    }

    protected Long addMovie(String adminToken, String title, int year, String genre, int duration, String director, String synopsis, int numberOfCopies) throws Exception {
        String movieJson = movieBody(title, year, genre, duration, director, synopsis, numberOfCopies);
        MockMultipartFile moviePart = new MockMultipartFile(
                "movie",
                "movie.json",
                MediaType.APPLICATION_JSON_VALUE,
                movieJson.getBytes()
        );

        MockMultipartFile emptyPoster = new MockMultipartFile(
                "poster",
                new byte[0]
        );

        String response = mockMvc.perform(multipart("/movies")
                        .file(moviePart)
                        .file(emptyPoster)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.parse(response).read("$.id", Long.class);
    }


    protected String movieBody(String title, int year, String genre, int duration, String director, String synopsis, int numberOfCopies) {
        return """
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
    }

    protected String updateMovieInfoBody(String title, int year, String genre, int duration, String director, String synopsis) {
        return """
                {
                      "title": "%s",
                      "year": "%d",
                      "genre": "%s",
                      "duration": "%d",
                      "director": "%s",
                      "synopsis": "%s"
                    }
                """.formatted(title, year, genre, duration, director, synopsis);
    }

    protected void rentMovie(String userToken, Long movieId) throws Exception {
        String body = rentalBody(movieId);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    protected String rentalBody(Long movieId) {
        return """
                {
                  "movieId": %d
                }
                """.formatted(movieId);
    }

    protected void addReview(String userToken, Long movieId, int rating, String comment) throws Exception {
        String body = reviewBody(movieId, rating, comment);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    protected String reviewBody(Long movieId, int rating, String comment) {
        return """
                {
                  "movieId": %d,
                  "rating": %d,
                  "comment": "%s"
                }
                """.formatted(movieId, rating, comment);
    }

    protected void addFavourite(String userToken, Long movieId) throws Exception {
        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favouriteBody(movieId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated());
    }

    protected String favouriteBody(Long movieId) {
        return """
                {
                  "movieId": %d
                }
                """.formatted(movieId);
    }
}
