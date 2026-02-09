package com.videostore.videostore.integration.user;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfiguration.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    private Long user1Id;
    private Long adminId;

    @BeforeEach
    void setUp() throws Exception {
        user1Id = registerUser("user1", "user1@test.com", "Password12345");
        registerUser("user2", "user2@test.com", "Password67890");

        adminId = registerAdmin();

        userToken = login("user1", "Password12345");
        adminToken = login("admin", "Admin1234");
    }

    @Test
    void removeUser_shouldWorkForAdmin() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user1Id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user1Id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeUser_shouldFailForNonexistentUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMe_shouldReturnAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    void getUser_shouldReturnUserForAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}", adminId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void getUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}", user1Id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_shouldFailForNonexistentUser() throws Exception {
        mockMvc.perform(get("/users/{userId}", 999L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnListForAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllUsers_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
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
}
