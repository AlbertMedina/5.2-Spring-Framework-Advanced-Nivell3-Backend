package com.videostore.videostore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
class VideostoreApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
