package com.videostore.videostore.web.controller.user;

import com.videostore.videostore.application.port.in.user.*;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.web.controller.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@Tag(name = "Users", description = "Operations related to users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final RemoveUserUseCase removeUserUseCase;
    private final GetMeUseCase getMeUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    public UserController(
            RemoveUserUseCase removeUserUseCase,
            GetMeUseCase getMeUseCase,
            GetUserUseCase getUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase
    ) {
        this.removeUserUseCase = removeUserUseCase;
        this.getMeUseCase = getMeUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
    }

    @Operation(summary = "Remove a user")
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public ResponseEntity<Void> removeUser(@PathVariable @Positive Long userId) {
        log.info("Admin requested deletion of user {}", userId);

        removeUserUseCase.execute(userId);

        log.info("User with id {} successfully deleted", userId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get details of authenticated user")
    @GetMapping("/me")
    @Cacheable(value = "users", key = "#authentication.name")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        log.info("Request received to get the authenticated user");

        User user = getMeUseCase.execute(authentication.getName());

        log.info("Authenticated user with id {} successfully retrieved", user.getId().value());

        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get details of a user")
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "users", key = "#userId")
    public ResponseEntity<UserResponse> getUser(@PathVariable @Positive Long userId) {
        log.info("Admin requested user {}", userId);

        User user = getUserUseCase.execute(userId);

        log.info("User with id {} successfully retrieved", userId);

        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get details of all users")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "allUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Admin requested all users");

        List<UserResponse> response = getAllUsersUseCase.execute()
                .stream().map(UserResponse::fromDomain).toList();

        log.info("All users successfully retrieved, total count: {}", response.size());

        return ResponseEntity.ok(response);
    }
}
