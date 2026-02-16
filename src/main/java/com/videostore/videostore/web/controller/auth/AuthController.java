package com.videostore.videostore.web.controller.auth;

import com.videostore.videostore.application.command.auth.LoginUserCommand;
import com.videostore.videostore.application.command.auth.RegisterUserCommand;
import com.videostore.videostore.application.port.in.auth.LoginUserUseCase;
import com.videostore.videostore.application.port.in.auth.RegisterUserUseCase;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.infrastructure.security.JwtService;
import com.videostore.videostore.web.controller.auth.dto.request.LoginUserRequest;
import com.videostore.videostore.web.controller.auth.dto.response.AuthUserResponse;
import com.videostore.videostore.web.controller.auth.dto.response.LoginResponse;
import com.videostore.videostore.web.controller.auth.dto.request.RegisterUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Authentication", description = "Operations related to user authentication (register/login)")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final JwtService jwtService;

    public AuthController(LoginUserUseCase loginUserUseCase,
                          JwtService jwtService,
                          RegisterUserUseCase registerUserUseCase
    ) {
        this.loginUserUseCase = loginUserUseCase;
        this.jwtService = jwtService;
        this.registerUserUseCase = registerUserUseCase;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/auth/register")
    public ResponseEntity<AuthUserResponse> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        log.info("Received request to register new user with username {}", request.username());

        RegisterUserCommand command = new RegisterUserCommand(
                request.name(),
                request.surname(),
                request.username(),
                request.email(),
                request.password()
        );
        User user = registerUserUseCase.execute(command);

        log.info("User with id {} and username {} successfully registered", user.getId().value(), user.getUsername().value());

        AuthUserResponse response = AuthUserResponse.fromDomain(user);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Login a user")
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginUserRequest request) {
        log.info("Received login request for identifier {}", request.loginIdentifier());

        LoginUserCommand command = new LoginUserCommand(
                request.loginIdentifier(),
                request.password()
        );
        User user = loginUserUseCase.execute(command);

        log.info("User with id {} and username {} successfully logged in", user.getId().value(), user.getUsername().value());

        String token = jwtService.generateToken(user.getUsername().value());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole()));
    }
}
