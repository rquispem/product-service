package com.rquispe.springcloud.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.profiles.active=native"})
public class ConfigServerApplicationTests {

    @Test
    public void contextLoads() {
    }
}
