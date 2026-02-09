package com.videostore.videostore.integration.auth;

import com.videostore.videostore.TestContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestContainersConfiguration.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void register_shouldCreateUserInDatabase() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void register_shouldFailWithExistingUsername() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user12345@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void register_shouldFailWithExistingEmail() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user12345",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void register_shouldFailWithInvalidUsername() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "invalid username",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_shouldFailWithInvalidEmail() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "invalid-email",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void register_shouldFailWithInvalidPassword() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "invalid-password"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void login_shouldWorkWithValidUsernameAndPassword() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "loginIdentifier": "user123",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldWorkWithValidEmailAndPassword() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "loginIdentifier": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldFailWithNonexistentUsernameOrEmail() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "loginIdentifier": "example",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldFailWithIncorrectPassword() throws Exception {
        String body = """
                    {
                      "name": "User",
                      "surname": "Example",
                      "username": "user123",
                      "email": "user123@test.com",
                      "password": "password12345"
                    }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = """
                    {
                      "loginIdentifier": "user123",
                      "password": "example"
                    }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isBadRequest());
    }
}