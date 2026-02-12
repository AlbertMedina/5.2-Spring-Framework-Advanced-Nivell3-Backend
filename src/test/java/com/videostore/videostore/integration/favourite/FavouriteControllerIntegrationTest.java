package com.videostore.videostore.integration.favourite;

import com.videostore.videostore.integration.AbstractIntegrationTest;
import com.videostore.videostore.integration.AuthenticatedTestUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavouriteControllerIntegrationTest extends AbstractIntegrationTest {

    private AuthenticatedTestUser admin;
    private AuthenticatedTestUser user;

    @BeforeEach
    void setUp() throws Exception {
        admin = registerAndLogin("Admin", "Test", "admin_test", "admin_test@test.com", "Password12345", true);
        user = registerAndLogin("User", "Example", "user1", "user1@test.com", "Password12345", false);
    }

    @Test
    void addFavourite_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        String body = favouriteBody(movieId);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());
    }

    @Test
    void addFavourite_shouldFailForUnauthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        String body = favouriteBody(movieId);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addFavourite_shouldFailWhenMovieDoesNotExist() throws Exception {
        String body = favouriteBody(999L);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addFavourite_shouldFailWhenFavouriteAlreadyExists() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);

        String body = favouriteBody(movieId);

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isConflict());
    }

    @Test
    void removeFavourite_shouldWorkForAuthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        addFavourite(user.token(), movieId);

        mockMvc.perform(delete("/favourites/{movieId}", movieId)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeFavourite_shouldFailForUnauthenticatedUser() throws Exception {
        Long movieId = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        addFavourite(user.token(), movieId);

        mockMvc.perform(delete("/favourites/{movieId}", movieId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeFavourite_shouldFailWhenFavouriteDoesNotExist() throws Exception {
        mockMvc.perform(delete("/favourites/{movieId}", 999L)
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyFavourites_shouldReturnListForAuthenticatedUser() throws Exception {
        Long movie1Id = addMovie(admin.token(), "Movie 1", 2000, "Action", 120, "Director A", "Synopsis 1", 1);
        addFavourite(user.token(), movie1Id);

        Long movie2Id = addMovie(admin.token(), "Movie 2", 2010, "Drama", 100, "Director B", "Synopsis 2", 1);
        addFavourite(user.token(), movie2Id);

        mockMvc.perform(get("/me/favourites")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMyFavourites_shouldReturnEmptyListWhenNoFavourites() throws Exception {
        mockMvc.perform(get("/me/favourites")
                        .header("Authorization", "Bearer " + user.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getMyFavourites_shouldFailForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/me/favourites"))
                .andExpect(status().isUnauthorized());
    }
}
