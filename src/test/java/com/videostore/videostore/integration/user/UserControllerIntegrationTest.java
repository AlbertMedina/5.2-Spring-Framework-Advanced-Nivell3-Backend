package com.videostore.videostore.integration.user;

import com.videostore.videostore.integration.AbstractIntegrationTest;
import com.videostore.videostore.integration.AuthenticatedTestUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private AuthenticatedTestUser admin;
    private AuthenticatedTestUser user;

    @BeforeEach
    void setUp() throws Exception {
        admin = registerAndLogin("Admin", "Test", "admin_test", "admin_test@test.com", "Password12345", true);
        user = registerAndLogin("User", "Example", "user1", "user1@test.com", "Password12345", false);
    }

    @Test
    void removeUser_shouldWorkForAdmin() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user.id())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(delete("/users/{userId}", user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeUser_shouldFailForNonexistentUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", 999L)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMe_shouldReturnAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@test.com"));
    }

    @Test
    void getUser_shouldReturnUserForAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}", user.id())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void getUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}", user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUser_shouldFailForNonexistentUser() throws Exception {
        mockMvc.perform(get("/users/{userId}", 999L)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnListForAdmin() throws Exception {
        registerAndLogin("User B", "Example B", "user2", "user2@test.com", "Password67890", false);

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllUsers_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }
}
