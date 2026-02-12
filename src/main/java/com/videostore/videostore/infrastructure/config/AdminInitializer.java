package com.videostore.videostore.infrastructure.config;

import com.videostore.videostore.domain.model.user.Role;
import com.videostore.videostore.domain.model.user.User;
import com.videostore.videostore.domain.model.user.valueobject.*;
import com.videostore.videostore.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Username adminUsername = new Username("admin");
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = User.create(
                    null,
                    new Name("Admin"),
                    new Surname("VideoStore"),
                    adminUsername,
                    new Email("admin@videostore.com"),
                    new Password(passwordEncoder.encode("admin123")),
                    Role.ADMIN
            );
            userRepository.registerUser(admin);
        }
    }
}
