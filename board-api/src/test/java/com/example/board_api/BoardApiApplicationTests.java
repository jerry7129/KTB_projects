package com.example.board_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.PropertySource;

@ActiveProfiles("development")
@TestPropertySource(properties = {
    "DB_USERNAME=springbootuser",
    "DB_PASSWORD=springboot030928",
    "JWT_SECRET=G5KqZvoFdwznjSmV/bfotdex9dgsTSwKB4iVb9Jd7Ey8RbAnub58E0RMkHSDXsmkJQ4Q6W/r/8ICMXCulnLxdw=="
})
@SpringBootTest
class BoardApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
