package com.videostore.videostore.integration.rental;

import com.videostore.videostore.integration.AbstractIntegrationTest;
import com.videostore.videostore.integration.AuthenticatedTestUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RentalControllerIntegrationTest extends AbstractIntegrationTest {

    private AuthenticatedTestUser admin;
    private AuthenticatedTestUser user;

    @BeforeEach
    void setUp() throws Exception {
        admin = registerAndLogin("Admin", "Test", "admin_test", "admin_test@test.com", "Password12345", true);
        user = registerAndLogin("User", "Example", "user1", "user1@test.com", "Password12345", false);
    }

    @Test
    void rentMovie_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());
    }

    @Test
    void rentMovie_shouldFailForUnauthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

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
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void rentMovie_shouldFailWhenMovieNotAvailable() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());

        AuthenticatedTestUser user2 = registerAndLogin("User B", "Example B", "user2", "user2@test.com", "Password67890", false);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user2.token()))
                .andExpect(status().isConflict());
    }

    @Test
    void rentMovie_shouldFailWhenMovieAlreadyRentedByUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalBody(movieId))
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isConflict());
    }

    @Test
    void returnMovie_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        rentMovie(user.token(), movieId);

        mockMvc.perform(delete("/rentals/{movieId}", movieId)
                        .header("Authorization", "Bearer " + user.token()))
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
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyRentals_shouldReturnListForAuthenticatedUser() throws Exception {
        Long movie1Id = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        rentMovie(user.token(), movie1Id);

        Long movie2Id = addMovie(admin.token(), "Movie 2", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        rentMovie(user.token(), movie2Id);

        mockMvc.perform(get("/me/rentals")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMyRentals_shouldReturnEmptyListWhenNoRentals() throws Exception {
        mockMvc.perform(get("/me/rentals")
                        .header("Authorization", "Bearer " + user.token()))
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
        Long movie1Id = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        rentMovie(user.token(), movie1Id);

        Long movie2Id = addMovie(admin.token(), "Movie 2", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        rentMovie(user.token(), movie2Id);

        mockMvc.perform(get("/users/{userId}/rentals", user.id())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRentalsByUser_shouldReturnEmptyListWhenUserHasNoRentals() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", user.id())
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRentalsByUser_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", user.id())
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRentalsByUser_shouldFailWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/{userId}/rentals", 999L)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRentalsByMovie_shouldReturnListForAdmin() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        AuthenticatedTestUser user2 = registerAndLogin("User B", "Example B", "user2", "user2@test.com", "Password67890", false);

        rentMovie(user.token(), movieId);
        rentMovie(user2.token(), movieId);

        mockMvc.perform(get("/movies/{movieId}/rentals", movieId)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getRentalsByMovie_shouldReturnEmptyListWhenMovieHasNoRentals() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        mockMvc.perform(get("/movies/{movieId}/rentals", movieId)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRentalsByMovie_shouldFailForNonAdmin() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/rentals", 1L)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRentalsByMovie_shouldFailWhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(get("/movies/{movieId}/rentals", 999L)
                        .header("Authorization", "Bearer " + admin.token()))
                .andExpect(status().isNotFound());
    }
}
