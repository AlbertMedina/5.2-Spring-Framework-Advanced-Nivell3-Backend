package com.videostore.videostore;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestVideostoreApiApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(VideostoreApiApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
