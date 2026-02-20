package com.videostore.videostore.integration.review;

import com.videostore.videostore.integration.AbstractIntegrationTest;
import com.videostore.videostore.integration.AuthenticatedTestUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerIntegrationTest extends AbstractIntegrationTest {

    private AuthenticatedTestUser admin;
    private AuthenticatedTestUser user;

    @BeforeEach
    void setUp() throws Exception {
        admin = registerAndLogin("Admin", "Test", "admin_test", "admin_test@test.com", "Password12345", true);
        user = registerAndLogin("User", "Example", "user1", "user1@test.com", "Password12345", false);
    }

    @Test
    void addReview_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
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
        String body = reviewBody(999L, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addReview_shouldFailWhenMovieIsNotRentedByUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isConflict());
    }

    @Test
    void addReview_shouldFailWhenMovieAlreadyReviewedByUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);

        String body = reviewBody(movieId, 5, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isConflict());
    }

    @Test
    void addReview_shouldFailWithInvalidRating() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);

        String body = reviewBody(movieId, -1, "Comment");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addReview_shouldFailWithInvalidComment() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);

        String body = reviewBody(movieId, 5, "");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeReview_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);

        Long reviewId = addReview(user.token(), movieId, 5, "Comment");

        mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeReview_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(delete("/reviews/{movieId}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeReview_shouldFailWhenReviewDoesNotExist() throws Exception {
        mockMvc.perform(delete("/reviews/{movieId}", 999L)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviewsByMovie_shouldReturnList() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        rentMovie(user.token(), movieId);
        addReview(user.token(), movieId, 5, "Comment 1");

        AuthenticatedTestUser user2 = registerAndLogin("User B", "Example B", "user2", "user2@test.com", "Password67890", false);
        rentMovie(user2.token(), movieId);
        addReview(user2.token(), movieId, 4, "Comment 2");

        mockMvc.perform(get("/movies/{movieId}/reviews", movieId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReviewsByMovie_shouldReturnEmptyListWhenNoReviews() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 2);

        mockMvc.perform(get("/movies/{movieId}/reviews", movieId)
                        .header("Authorization", "Bearer " + user.token()))
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
        mockMvc.perform(get("/movies/{movieId}/reviews", 999L)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }
}
