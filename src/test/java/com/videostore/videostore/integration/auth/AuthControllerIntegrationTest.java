package com.videostore.videostore.integration.auth;

import com.videostore.videostore.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void register_shouldCreateUserInDatabase() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void register_shouldFailWithExistingUsername() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        body = registerBody("User B", "Example B", "user1", "user2@test.com", "Password67890");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void register_shouldFailWithExistingEmail() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        body = registerBody("User B", "Example B", "user2", "user1@test.com", "Password67890");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void register_shouldFailWithInvalidUsername() throws Exception {
        String body = registerBody("User", "Example", "invalid username", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldFailWithInvalidEmail() throws Exception {
        String body = registerBody("User", "Example", "user1", "invalid-email", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldFailWithInvalidPassword() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "invalid-password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldWorkWithValidUsernameAndPassword() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = loginBody("user1", "Password12345");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldWorkWithValidEmailAndPassword() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = loginBody("user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_shouldFailWithNonexistentUsernameOrEmail() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = loginBody("example", "Password12345");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldFailWithIncorrectPassword() throws Exception {
        String body = registerBody("User", "Example", "user1", "user1@test.com", "Password12345");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        String loginBody = loginBody("user1", "example");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isBadRequest());
    }
}