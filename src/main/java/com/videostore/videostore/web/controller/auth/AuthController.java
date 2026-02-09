package com.videostore.videostore.web.controller.auth;

import com.videostore.videostore.application.command.auth.LoginUserCommand;
import com.videostore.videostore.application.command.auth.RegisterUserCommand;
import com.videostore.videostore.application.port.in.auth.LoginUserUseCase;
import com.videostore.videostore.application.port.in.auth.RegisterUserUseCase;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.infrastructure.security.JwtService;
import com.videostore.videostore.web.controller.auth.dto.request.LoginUserRequest;
import com.videostore.videostore.web.controller.auth.dto.response.LoginResponse;
import com.videostore.videostore.web.controller.auth.dto.request.RegisterUserRequest;
import com.videostore.videostore.web.controller.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class AuthController {

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

    @PostMapping("/auth/register")
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
}
