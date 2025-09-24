package com.zfh.tastenotesapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class StarLifeAppApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(LocalDateTime.now().getDayOfWeek());
    }

}
