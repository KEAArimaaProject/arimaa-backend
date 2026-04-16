package com.example.arimaabackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
class ArimaaBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        assertTrue(1 == 1, "1 should equal 1");   // message is optional but recommended
    }


}
