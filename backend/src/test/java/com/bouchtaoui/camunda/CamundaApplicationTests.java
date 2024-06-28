package com.bouchtaoui.camunda;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import io.camunda.zeebe.spring.test.ZeebeSpringTest;

@TestPropertySource(properties = { "spring.config.name=test" })
@ZeebeSpringTest
@SpringBootTest
class CamundaApplicationTests {

    @Test
    void contextLoads() {
    }

}