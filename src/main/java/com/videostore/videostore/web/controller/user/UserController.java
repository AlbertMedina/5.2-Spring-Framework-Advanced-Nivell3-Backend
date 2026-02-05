package com.videostore.videostore.web.controller.user;

import com.videostore.videostore.application.command.user.LoginUserCommand;
import com.videostore.videostore.application.command.user.RegisterUserCommand;
import com.videostore.videostore.application.port.in.user.*;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.infrastructure.security.JwtService;
import com.videostore.videostore.web.controller.user.dto.request.LoginUserRequest;
import com.videostore.videostore.web.controller.user.dto.request.RegisterUserRequest;
import com.videostore.videostore.web.controller.user.dto.response.LoginResponse;
import com.videostore.videostore.web.controller.user.dto.response.UserResponse;
import jakarta.validation.Valid;
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

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RemoveUserUseCase removeUserUseCase;
    private final GetMeUseCase getMeUseCase;
    private final GetUserUseCase getUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final JwtService jwtService;

    public UserController(
            RegisterUserUseCase registerUserUseCase,
            LoginUserUseCase loginUserUseCase,
            RemoveUserUseCase removeUserUseCase,
            GetMeUseCase getMeUseCase,
            GetUserUseCase getUserUseCase,
            GetAllUsersUseCase getAllUsersUseCase,
            JwtService jwtService
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.removeUserUseCase = removeUserUseCase;
        this.getMeUseCase = getMeUseCase;
        this.getUserUseCase = getUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.surname(),
                request.username(),
                request.email(),
                request.password()
        );
        User user = registerUserUseCase.execute(command);

        UserResponse response = UserResponse.fromDomain(user);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginUserRequest request) {
        LoginUserCommand command = new LoginUserCommand(
                request.loginIdentifier(),
                request.password()
        );
        User user = loginUserUseCase.execute(command);

        String token = jwtService.generateToken(user.getUsername().value());
        return ResponseEntity.ok(new LoginResponse(token));
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
