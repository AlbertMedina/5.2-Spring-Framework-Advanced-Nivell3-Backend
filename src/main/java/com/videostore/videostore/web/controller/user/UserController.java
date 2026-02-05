package com.videostore.videostore.web.controller.user;

import com.videostore.videostore.application.port.in.user.*;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.web.controller.user.dto.response.UserResponse;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class UserController {

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

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeUser(@PathVariable @Positive Long userId) {
        removeUserUseCase.execute(userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        User user = getMeUseCase.execute(authentication.getName());

        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable @Positive Long userId) {
        User user = getUserUseCase.execute(userId);

        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = getAllUsersUseCase.execute()
                .stream().map(UserResponse::fromDomain).toList();

        return ResponseEntity.ok(response);
    }
}
